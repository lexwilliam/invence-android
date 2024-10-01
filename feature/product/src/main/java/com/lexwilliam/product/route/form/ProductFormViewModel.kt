package com.lexwilliam.product.route.form

import android.graphics.Bitmap
import android.graphics.RectF
import android.net.Uri
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.example.barcode.analyzer.BarcodeImageAnalyzer
import com.example.barcode.analyzer.BarcodeResultBoundaryAnalyzer
import com.example.barcode.model.BarCodeResult
import com.example.barcode.model.ScanningResult
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.lexwilliam.core.extensions.addOrUpdateDuplicate
import com.lexwilliam.inventory.scan.ProductScanEvent
import com.lexwilliam.product.model.Product
import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.model.ProductItem
import com.lexwilliam.product.model.UiPriceAndQuantity
import com.lexwilliam.product.navigation.ProductFormNavigationTarget
import com.lexwilliam.product.route.form.scan.ProductScanBottomSheetState
import com.lexwilliam.product.usecase.ObserveProductCategoryUseCase
import com.lexwilliam.product.usecase.UploadCategoryImageUseCase
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
        private val uploadProductCategoryImage: UploadCategoryImageUseCase,
        private val barcodeImageAnalyzer: BarcodeImageAnalyzer,
        private val barcodeResultBoundaryAnalyzer: BarcodeResultBoundaryAnalyzer,
        observeSession: ObserveSessionUseCase,
        fetchUser: FetchUserUseCase,
        savedStateHandle: SavedStateHandle
    ) : ViewModel() {
        val productUUID =
            savedStateHandle
                .getStateFlow<String?>("productUUID", null)
                .onEach { _state.update { old -> old.copy(upc = it) } }

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

        val categories =
            _categories
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

        private val category =
            _categories.map { categories ->
                categories.firstOrNull { category ->
                    category.products.any { product ->
                        product.sku == productUUID.firstOrNull()
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
                            product.sku == productUUID
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
                    sku = product.sku,
                    upc = product.upc,
                    title = product.name,
                    image = product.imagePath,
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
                _state.update { old -> old.copy(upc = productUUID.firstOrNull()) }
            }
        }

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
                is ProductFormUiEvent.InputImageChanged -> handleInputImageChanged(event.bmp)
                is ProductFormUiEvent.CameraClicked -> handleCameraClicked()
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
                is ProductFormUiEvent.CategorySelected -> handleCategorySelected(event.category)
                is ProductFormUiEvent.AddCategory -> handleAddCategory(event.name, event.bitmap)
                ProductFormUiEvent.CategoryDismiss -> handleCategoryDismiss()
                is ProductFormUiEvent.SkuChanged -> handleSkuChanged(event.value)
                is ProductFormUiEvent.UpcChanged -> handleUpcChanged(event.value)
                ProductFormUiEvent.GenerateSkuClicked -> handleGenerateSkuClicked()
            }
        }

        private fun handleGenerateSkuClicked() {
            val code = UUID.randomUUID().toString().split("-").firstOrNull()
            _state.update { old -> old.copy(sku = code ?: "") }
        }

        private fun handleUpcChanged(value: String) {
            _state.update { old -> old.copy(upc = value) }
        }

        private fun handleSkuChanged(value: String) {
            _state.update { old -> old.copy(sku = value) }
        }

        private fun handleCategoryDismiss() {
            _state.update { old -> old.copy(isSelectCategoryShowing = false) }
        }

        private fun handleAddCategory(
            name: String,
            imagePath: Bitmap?
        ) {
            viewModelScope.launch {
                val branchUUID = branchUUID.firstOrNull() ?: return@launch
                val categoryUUID = UUID.randomUUID()
                val image = imagePath ?: return@launch
                uploadProductCategoryImage(
                    branchUUID = branchUUID,
                    categoryUUID = categoryUUID,
                    bmp = image
                ).fold(
                    ifLeft = {
                        Log.d("TAG", it.toString())
                    },
                    ifRight = {
                        val category =
                            ProductCategory(
                                uuid = categoryUUID,
                                branchUUID = branchUUID,
                                imageUrl = it,
                                name = name,
                                products = emptyList(),
                                createdAt = Clock.System.now(),
                                deletedAt = null
                            )
                        when (val result = upsertProductCategory(category = category)) {
                            is Either.Left -> Log.d("TAG", result.value.toString())
                            is Either.Right -> Log.d("TAG", "Success")
                        }
                    }
                )
            }
        }

        private fun handleCategorySelected(category: ProductCategory) {
            _state.update { old ->
                old.copy(
                    selectedCategory = category,
                    isSelectCategoryShowing = false
                )
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

        private fun handleAddProductItem() {
            _state.update { old ->
                val emptyProductItem =
                    (old.buyPriceList.size + 1) to
                        UiPriceAndQuantity(price = "", quantity = "")
                old.copy(buyPriceList = old.buyPriceList + emptyProductItem)
            }
        }

        private fun handleInputImageChanged(bmp: Bitmap?) {
            _state.update { old ->
                old.copy(image = bmp)
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
            _state.update { old -> old.copy(isSelectCategoryShowing = true) }
        }

        private fun handleDescriptionValueChanged(value: String) {
            _state.update { old -> old.copy(description = value) }
        }

        private fun handleSaveClicked() {
            _state.update { old -> old.copy(isLoading = true) }
            viewModelScope.launch {
                val category = _state.value.selectedCategory ?: return@launch
                val upc = _state.value.upc ?: return@launch
                val branchUUID = branchUUID.firstOrNull() ?: return@launch
                var product =
                    Product(
                        sku = _state.value.sku,
                        upc = upc,
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
                val image = _state.value.image
                if (image != null && image is Bitmap) {
                    uploadProductImage(
                        branchUUID = branchUUID,
                        productUUID = product.sku,
                        bmp = image
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
                if (image is Uri) {
                    product = product.copy(imagePath = image)
                }
                val modifiedCategory =
                    category
                        .copy(
                            products =
                                category.products
                                    .addOrUpdateDuplicate(product) { e, n ->
                                        e.sku == n.sku
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
            _state.update { old -> old.copy(upc = barcode, isScanBarcodeShowing = false) }
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