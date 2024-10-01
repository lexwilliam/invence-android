package com.lexwilliam.product.route.form

import android.graphics.Bitmap
import android.graphics.RectF
import android.net.Uri
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barcode.analyzer.BarcodeImageAnalyzer
import com.example.barcode.analyzer.BarcodeResultBoundaryAnalyzer
import com.example.barcode.model.BarCodeResult
import com.example.barcode.model.ScanningResult
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.lexwilliam.core.extensions.addOrUpdateDuplicate
import com.lexwilliam.core.extensions.isFirebaseUri
import com.lexwilliam.core.model.UploadImageFormat
import com.lexwilliam.inventory.scan.ProductScanEvent
import com.lexwilliam.product.category.CategoryUiEvent
import com.lexwilliam.product.category.CategoryUiState
import com.lexwilliam.product.model.Product
import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.model.ProductItem
import com.lexwilliam.product.model.UiPriceAndQuantity
import com.lexwilliam.product.navigation.ProductFormNavigationTarget
import com.lexwilliam.product.route.form.scan.ProductScanBottomSheetState
import com.lexwilliam.product.usecase.ObserveProductCategoryUseCase
import com.lexwilliam.product.usecase.UploadProductCategoryUseCase
import com.lexwilliam.product.usecase.UploadProductImageUseCase
import com.lexwilliam.product.usecase.UpsertProductCategoryUseCase
import com.lexwilliam.user.usecase.FetchUserUseCase
import com.lexwilliam.user.usecase.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

@ExperimentalGetImage
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductFormViewModel
    @Inject
    constructor(
        observeProductCategory: ObserveProductCategoryUseCase,
        private val upsertProductCategory: UpsertProductCategoryUseCase,
        private val uploadProductImage: UploadProductImageUseCase,
        private val uploadProductCategoryImage: UploadProductCategoryUseCase,
        private val barcodeImageAnalyzer: BarcodeImageAnalyzer,
        private val barcodeResultBoundaryAnalyzer: BarcodeResultBoundaryAnalyzer,
        observeSession: ObserveSessionUseCase,
        fetchUser: FetchUserUseCase,
        savedStateHandle: SavedStateHandle
    ) : ViewModel() {
        val productUUID =
            savedStateHandle
                .getStateFlow<String?>("productUUID", null)
                .onEach { _state.update { old -> old.copy(uuid = it) } }

        private val branchUUID =
            observeSession().map { session ->
                session.userUUID
                    ?.let { fetchUser(it) }
                    ?.getOrNull()
                    ?.branchUUID
            }

        private val _categories =
            branchUUID.flatMapLatest {
                when (it) {
                    null -> flowOf(emptyList())
                    else -> observeProductCategory(it)
                }
            }

        private val category =
            _categories.map { categories ->
                categories.firstOrNull { category ->
                    category.products.any { product ->
                        product.uuid == productUUID.firstOrNull()
                    }
                }
            }.onEach { category ->
                _state.update { old -> old.copy(selectedCategory = category) }
            }

        val product =
            combine(
                productUUID,
                category
            ) { productUUID, category ->
                productUUID?.let {
                    val product =
                        category?.products?.firstOrNull { product ->
                            product.uuid == productUUID
                        }
                    product?.let { updateProductState(it) } ?: Product()
                } ?: Product()
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                Product()
            )

        private fun updateProductState(product: Product) {
            _state.update {
                    old ->
                old.copy(
                    title = product.name,
                    image = product.imagePath?.let { UploadImageFormat.WithUri(it) },
                    sellPrice = product.sellPrice.toString(),
                    buyPriceList =
                        product.items.associate {
                                item ->
                            item.itemId to
                                UiPriceAndQuantity(
                                    item.buyPrice.toString(),
                                    item.quantity.toString()
                                )
                        },
                    description = product.description
                )
            }
        }

        private val _state = MutableStateFlow(ProductFormUiState())
        val uiState = _state.asStateFlow()

        init {
            viewModelScope.launch {
                _state.update { old -> old.copy(uuid = productUUID.firstOrNull()) }
            }
        }

        private val _categoryState = MutableStateFlow<CategoryUiState?>(null)
        val categoryState = _categoryState.asStateFlow()

        private val _navigation = Channel<ProductFormNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        // Barcode Dialog
        private val _scanningResult = MutableStateFlow<ScanningResult>(ScanningResult.Initial())
        val scanningResult = _scanningResult.asStateFlow()

        private val _freezeCameraPreview = Channel<Boolean>()
        val freezeCameraPreview = _freezeCameraPreview.receiveAsFlow()

        private val _resultBottomSheetState =
            MutableStateFlow<ProductScanBottomSheetState>(ProductScanBottomSheetState.Hidden)
        val resultBottomSheetState =
            _resultBottomSheetState.asStateFlow()

        private var loadingProductCode: String = ""
        private var isResultBottomSheetShowing = false
        private lateinit var barcodeResult: BarCodeResult

        fun getBarcodeImageAnalyzer() = barcodeImageAnalyzer

        fun onEvent(event: ProductFormUiEvent) {
            when (event) {
                is ProductFormUiEvent.BackStackClicked -> handleBackStackClicked()
                is ProductFormUiEvent.InputImageChanged -> handleInputImageChanged(event.uri)
                is ProductFormUiEvent.CameraClicked -> handleCameraClicked()
                is ProductFormUiEvent.ProductPhotoTaken -> handleProductPhotoTaken(event.bitmap)
                is ProductFormUiEvent.ScanBarcodeClicked -> handleScanBarcodeClicked()
                is ProductFormUiEvent.TitleValueChanged -> handleTitleValueChanged(event.value)
                ProductFormUiEvent.AddProductItem -> handleAddProductItem()
                is ProductFormUiEvent.RemoveProductItem -> handleRemoveProductItem(event.itemId)
                is ProductFormUiEvent.BuyPriceValueChanged ->
                    handleBuyPriceChanged(
                        event.itemId,
                        event.value
                    )
                is ProductFormUiEvent.SellPriceValueChanged -> handleSellPriceChanged(event.value)
                is ProductFormUiEvent.QuantityValueChanged ->
                    handleQuantityChanged(
                        event.itemId,
                        event.value
                    )
                is ProductFormUiEvent.SelectCategoryClicked ->
                    handleSelectCategoryClicked()
                is ProductFormUiEvent.DescriptionValueChanged ->
                    handleDescriptionValueChanged(
                        event.value
                    )
                is ProductFormUiEvent.SaveClicked -> handleSaveClicked()
            }
        }

        private fun handleRemoveProductItem(itemId: Int) {
            _state.update { old ->
                old.copy(
                    buyPriceList =
                        old.buyPriceList
                            .filterNot { entry -> entry.key == itemId }
                )
            }
        }

        private fun handleCameraClicked() {
            _state.update { old -> old.copy(takePhoto = true) }
        }

        private fun handleProductPhotoTaken(bitmap: Bitmap) {
            _state.update { old ->
                old.copy(
                    image = UploadImageFormat.WithBitmap(bitmap),
                    takePhoto = false
                )
            }
        }

        private fun handleAddProductItem() {
            _state.update { old ->
                val emptyProductItem =
                    (old.buyPriceList.size + 1) to
                        UiPriceAndQuantity(price = "", quantity = "")
                old.copy(buyPriceList = old.buyPriceList + emptyProductItem)
            }
        }

        private fun handleInputImageChanged(uri: Uri?) {
            _state.update { old ->
                old.copy(image = uri?.let { UploadImageFormat.WithUri(it) })
            }
        }

        private fun handleBackStackClicked() {
            viewModelScope.launch {
                _navigation.send(ProductFormNavigationTarget.BackStack)
            }
        }

        private fun handleScanBarcodeClicked() {
            _state.update { old -> old.copy(isScanBarcodeShowing = true) }
            setupImageAnalyzer()
        }

        private fun handleTitleValueChanged(value: String) {
            _state.update { old -> old.copy(title = value) }
        }

        private fun handleBuyPriceChanged(
            itemId: Int,
            value: String
        ) {
            _state.update { old ->
                old.copy(
                    buyPriceList =
                        old
                            .buyPriceList
                            .toMutableMap()
                            .apply {
                                this[itemId] =
                                    UiPriceAndQuantity(
                                        price = value,
                                        quantity = this[itemId]?.quantity ?: "0"
                                    )
                            }
                )
            }
        }

        private fun handleQuantityChanged(
            itemId: Int,
            value: String
        ) {
            _state.update { old ->
                old.copy(
                    buyPriceList =
                        old
                            .buyPriceList
                            .toMutableMap()
                            .apply {
                                this[itemId] =
                                    UiPriceAndQuantity(
                                        price = this[itemId]?.price ?: "0",
                                        quantity = value
                                    )
                            }
                )
            }
        }

        private fun handleSellPriceChanged(value: String) {
            _state.update { old -> old.copy(sellPrice = value) }
        }

        private fun handleSelectCategoryClicked() {
            viewModelScope.launch {
                _categoryState.update {
                    CategoryUiState(
                        categories = _categories.firstOrNull() ?: emptyList()
                    )
                }
            }
        }

        private fun handleDescriptionValueChanged(value: String) {
            _state.update { old -> old.copy(description = value) }
        }

        private fun handleSaveClicked() {
            _state.update { old -> old.copy(isLoading = true) }
            viewModelScope.launch {
                val category = _state.value.selectedCategory ?: return@launch
                val productUUID = _state.value.uuid ?: return@launch
                val branchUUID = branchUUID.firstOrNull() ?: return@launch
                var product =
                    Product(
                        uuid = productUUID,
                        name = _state.value.title,
                        description = _state.value.description,
                        categoryName = category.name,
                        sellPrice = _state.value.sellPrice.toDouble(),
                        items =
                            _state.value.buyPriceList.map { (id, item) ->
                                ProductItem(
                                    itemId = id,
                                    buyPrice = item.price.toDouble(),
                                    quantity = item.quantity.toInt(),
                                    createdAt = Clock.System.now()
                                )
                            },
                        createdAt = Clock.System.now()
                    )
                val format = _state.value.image
                if (format != null && !format.isFirebaseUri()) {
                    uploadProductImage(
                        branchUUID = branchUUID,
                        productUUID = product.uuid,
                        format = format
                    ).fold(
                        ifLeft = { failure ->
                            Log.d("TAG", failure.toString())
                            _state.update { old -> old.copy(isLoading = false) }
                        },
                        ifRight = { uri ->
                            product = product.copy(imagePath = uri)
                        }
                    )
                }
                if (format.isFirebaseUri()) {
                    when (format) {
                        is UploadImageFormat.WithUri ->
                            product = product.copy(imagePath = format.uri)
                        else -> {}
                    }
                }
                val modifiedCategory =
                    category
                        .copy(
                            products =
                                category.products
                                    .addOrUpdateDuplicate(product) { e, n ->
                                        e.uuid == n.uuid
                                    }
                        )
                upsertProductCategory(modifiedCategory).fold(
                    ifLeft = { failure ->
                        Log.d("TAG", failure.toString())
                        _state.update { old -> old.copy(isLoading = false) }
                    },
                    ifRight = {
                        _state.update { old -> old.copy(isLoading = false) }
                        _navigation.send(ProductFormNavigationTarget.BackStack)
                    }
                )
            }
        }

        fun onCategoryDialogEvent(event: CategoryUiEvent) {
            when (event) {
                is CategoryUiEvent.QueryChanged -> handleCategoryQueryChanged(event.value)
                is CategoryUiEvent.CategoryClicked -> handleCategoryClicked(event.item)
                CategoryUiEvent.Dismiss -> handleCategoryDismiss()
                is CategoryUiEvent.InputImageChanged -> handleCategoryInputImageChanged(event.uri)
                is CategoryUiEvent.ShowForm -> handleCategoryShowForm(event.show)
                is CategoryUiEvent.AddCategoryCameraClicked -> handleAddCategoryCameraClicked()
                is CategoryUiEvent.AddCategoryPhotoTaken ->
                    handleAddCategoryPhotoTaken(
                        event.bitmap
                    )
                is CategoryUiEvent.AddCategoryTitleChanged ->
                    handleAddCategoryNameChanged(
                        event.value
                    )
                CategoryUiEvent.AddCategoryConfirm -> handleAddCategoryConfirm()
            }
        }

        private fun handleAddCategoryPhotoTaken(bitmap: Bitmap) {
            _categoryState.update { old ->
                old?.copy(
                    formImagePath = UploadImageFormat.WithBitmap(bitmap),
                    takePhoto = false
                )
            }
        }

        private fun handleAddCategoryCameraClicked() {
            _categoryState.update { old -> old?.copy(takePhoto = true) }
        }

        private fun handleCategoryShowForm(show: Boolean) {
            _categoryState.update { old -> old?.copy(isFormShown = show) }
        }

        private fun handleCategoryDismiss() {
            _categoryState.update { null }
        }

        private fun handleCategoryClicked(category: ProductCategory) {
            _state.update {
                    old ->
                old.copy(selectedCategory = category)
            }
            _categoryState.update { null }
        }

        private fun handleAddCategoryConfirm() {
            viewModelScope.launch {
                val categoryState = _categoryState.value ?: return@launch
                val branchUUID = branchUUID.firstOrNull() ?: return@launch
                val categoryUUID = UUID.randomUUID()
                val category =
                    ProductCategory(
                        uuid = categoryUUID,
                        branchUUID = branchUUID,
                        name = categoryState.formTitle,
                        createdAt = Clock.System.now()
                    )
                if (categoryState.formImagePath != null) {
                    uploadProductCategoryImage(
                        branchUUID = branchUUID,
                        categoryUUID = categoryUUID,
                        format = categoryState.formImagePath
                    ).fold(
                        ifLeft = { failure ->
                            Log.d("TAG", failure.toString())
                        },
                        ifRight = { uri ->
                            upsertProductCategory(category.copy(imageUrl = uri)).fold(
                                ifLeft = { failure ->
                                    Log.d("TAG", failure.toString())
                                },
                                ifRight = {
                                    _categoryState.update { null }
                                }
                            )
                        }
                    )
                } else {
                    upsertProductCategory(category).fold(
                        ifLeft = { failure ->
                            Log.d("TAG", failure.toString())
                        },
                        ifRight = {
                            _categoryState.update { null }
                        }
                    )
                }
            }
        }

        private fun handleAddCategoryNameChanged(value: String) {
            _categoryState.update { old -> old?.copy(formTitle = value) }
        }

        private fun handleCategoryInputImageChanged(uri: Uri?) {
            _categoryState.update { old ->
                old?.copy(formImagePath = uri?.let { UploadImageFormat.WithUri(it) })
            }
        }

        private fun handleCategoryQueryChanged(value: String) {
            _categoryState.update { old -> old?.copy(query = value) }
        }

        fun onScanEvent(event: ProductScanEvent) {
            when (event) {
                is ProductScanEvent.BottomSheetDialogStateChanged ->
                    handleBottomSheetDialogStateChanged(
                        event.expanded
                    )
                is ProductScanEvent.CameraBoundaryReady ->
                    handleCameraBoundaryReady(
                        event.cameraBoundary
                    )
                ProductScanEvent.Dismiss -> handleDismiss()
                is ProductScanEvent.ScanningAreaReady -> handleScanningAreaReady(event.scanningArea)
                ProductScanEvent.BottomSheetDismiss -> handleBottomSheetDismiss()
                ProductScanEvent.ConfirmClicked -> handleScanConfirmClicked()
            }
        }

        private fun handleScanConfirmClicked() {
            val scanningResult = _scanningResult.value
            val barcode = scanningResult.barCodeResult?.barCode?.displayValue ?: return
            _state.update { old -> old.copy(uuid = barcode, isScanBarcodeShowing = false) }
            _resultBottomSheetState.update { ProductScanBottomSheetState.Hidden }
        }

        private fun handleBottomSheetDismiss() {
            _resultBottomSheetState.update { ProductScanBottomSheetState.Hidden }
        }

        private fun handleBottomSheetDialogStateChanged(expanded: Boolean) {
            isResultBottomSheetShowing =
                when {
                    expanded -> {
                        true
                    }
                    else -> {
                        freezeCameraPreview(false)
                        false
                    }
                }
        }

        private fun handleCameraBoundaryReady(cameraBoundary: RectF) {
            barcodeResultBoundaryAnalyzer.onCameraBoundaryReady(cameraBoundary)
        }

        private fun handleDismiss() {
            _state.update { old -> old.copy(isScanBarcodeShowing = false) }
        }

        private fun handleScanningAreaReady(scanningArea: RectF) {
            barcodeResultBoundaryAnalyzer.onBarcodeScanningAreaReady(scanningArea)
        }

        private fun setupImageAnalyzer() {
            barcodeImageAnalyzer.setProcessListener(
                listener =
                    object : BarcodeImageAnalyzer.ProcessListenerAdapter() {
                        override fun onSucceed(
                            results: List<Barcode>,
                            inputImage: InputImage
                        ) {
                            super.onSucceed(results, inputImage)
                            handleBarcodeResults(results, inputImage)
                        }
                    }
            )
        }

        private fun handleBarcodeResults(
            results: List<Barcode>,
            inputImage: InputImage
        ) {
            viewModelScope.launch(
                CoroutineExceptionHandler { _, exception ->
                    notifyErrorResult(exception)
                    Log.e("TAG", "$exception")
                }
            ) {
                if (isResultBottomSheetShowing) {
                    // skip the analyzer process if the result bottom sheet is showing
                    return@launch
                }

                val scanningResult = barcodeResultBoundaryAnalyzer.analyze(results, inputImage)
                _scanningResult.value = scanningResult
                if (scanningResult is ScanningResult.PerfectMatch) {
                    loadProductDetailsWithBarcodeResult(scanningResult)
                }
            }
        }

        private fun notifyErrorResult(exception: Throwable) {
            _resultBottomSheetState.value =
                ProductScanBottomSheetState.Error.Generic
        }

        private fun loadProductDetailsWithBarcodeResult(
            scanningResult: ScanningResult.PerfectMatch
        ) {
            val productCode = scanningResult.barCodeResult.barCode.displayValue
            if (productCode != null) {
                loadingProductCode = productCode
                showBottomSheetLoading(scanningResult.barCodeResult)
                freezeCameraPreview(true)
                bindBottomSheetResult(
                    barcodeResult = scanningResult.barCodeResult
                )
            } else {
                // Show Error Information
            }
        }

        private fun showBottomSheetLoading(barcodeResult: BarCodeResult) {
            isResultBottomSheetShowing = true
            _resultBottomSheetState.value = ProductScanBottomSheetState.Loading(barcodeResult)
            this.barcodeResult = barcodeResult
        }

        private fun bindBottomSheetResult(barcodeResult: BarCodeResult) {
            ProductScanBottomSheetState.ScanResult(
                barcodeResult = barcodeResult
            ).also { _resultBottomSheetState.value = it }
            this.barcodeResult = barcodeResult
        }

        private fun freezeCameraPreview(freeze: Boolean) {
            // true - freeze the camera preview,
            // false - resume the camera preview
            viewModelScope.launch {
                _freezeCameraPreview.send(freeze)
            }
        }
    }