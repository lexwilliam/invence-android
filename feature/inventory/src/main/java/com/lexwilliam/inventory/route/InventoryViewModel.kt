package com.lexwilliam.inventory.route

import android.graphics.RectF
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barcode.analyzer.BarcodeImageAnalyzer
import com.example.barcode.analyzer.BarcodeResultBoundaryAnalyzer
import com.example.barcode.model.BarCodeResult
import com.example.barcode.model.InformationModel
import com.example.barcode.model.ScanningResult
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.lexwilliam.inventory.model.UiProduct
import com.lexwilliam.inventory.navigation.InventoryNavigationTarget
import com.lexwilliam.inventory.scan.InventoryScanBottomSheetState
import com.lexwilliam.inventory.scan.ScanEvent
import com.lexwilliam.product.model.Product
import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.usecase.ObserveProductCategoryUseCase
import com.lexwilliam.product.util.queryProductCategory
import com.lexwilliam.user.usecase.FetchUserUseCase
import com.lexwilliam.user.usecase.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalGetImage
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class InventoryViewModel
    @Inject
    constructor(
        private val barcodeImageAnalyzer: BarcodeImageAnalyzer,
        private val barcodeResultBoundaryAnalyzer: BarcodeResultBoundaryAnalyzer,
        observeProductCategory: ObserveProductCategoryUseCase,
        observeSession: ObserveSessionUseCase,
        fetchUser: FetchUserUseCase
    ) : ViewModel() {
        private val _state = MutableStateFlow(InventoryUiState())
        val uiState = _state.asStateFlow()

        private val _navigation = Channel<InventoryNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

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
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5_000),
                    emptyList()
                )

        val uiProducts =
            _state.flatMapLatest { state ->
                branchUUID.flatMapLatest {
                    when (it) {
                        null -> flowOf(emptyList())
                        else ->
                            observeProductCategory(it)
                                .map { categories ->
                                    queryProductCategory(
                                        categories,
                                        state.query
                                    ).flatMap { category ->
                                        category.products.map { product ->
                                            UiProduct(
                                                category = category,
                                                product = product
                                            )
                                        }
                                    }
                                }
                    }
                }
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

        // Barcode Dialog
        private val _scanningResult = MutableStateFlow<ScanningResult>(ScanningResult.Initial())
        val scanningResult = _scanningResult.asStateFlow()

        private val _freezeCameraPreview = Channel<Boolean>()
        val freezeCameraPreview = _freezeCameraPreview.receiveAsFlow()

        private val _resultBottomSheetState =
            MutableStateFlow<InventoryScanBottomSheetState>(InventoryScanBottomSheetState.Hidden)
        val resultBottomSheetState =
            _resultBottomSheetState.asStateFlow()

        private var loadingProductCode: String = ""
        private var isResultBottomSheetShowing = false
        private lateinit var barcodeResult: BarCodeResult

        fun getBarcodeImageAnalyzer() = barcodeImageAnalyzer

        fun onEvent(event: InventoryUiEvent) {
            when (event) {
                is InventoryUiEvent.QueryChanged -> handleQueryChanged(event.value)
                is InventoryUiEvent.FabClicked -> handleFabClicked()
                is InventoryUiEvent.ProductClicked -> handleProductClicked(event.product)
                InventoryUiEvent.BarcodeScannerClicked -> handleBarcodeScannerClicked()
                is InventoryUiEvent.CategoryClicked -> handleCategoryClicked(event.category)
                InventoryUiEvent.CategorySettingClicked -> handleCategorySettingClicked()
                InventoryUiEvent.BackStackClicked -> handleBackStackClicked()
            }
        }

        private fun handleBackStackClicked() {
            viewModelScope.launch {
                _navigation.send(InventoryNavigationTarget.BackStack)
                _state.update { old -> old.copy(isScanBarcodeShowing = false) }
            }
        }

        private fun handleCategorySettingClicked() {
            viewModelScope.launch {
                _navigation.send(InventoryNavigationTarget.Category)
                _state.update { old -> old.copy(isScanBarcodeShowing = false) }
            }
        }

        private fun handleCategoryClicked(category: ProductCategory?) {
            _state.update { old ->
                old.copy(
                    query = old.query.copy(categoryUUID = category?.uuid)
                )
            }
        }

        private fun handleBarcodeScannerClicked() {
            _state.update { old -> old.copy(isScanBarcodeShowing = true) }
            setupImageAnalyzer()
        }

        private fun handleProductClicked(product: Product) {
            viewModelScope.launch {
                _navigation.send(InventoryNavigationTarget.ProductDetail(product.sku))
                _state.update { old -> old.copy(isScanBarcodeShowing = false) }
            }
        }

        private fun handleQueryChanged(value: String) {
            _state.update { old ->
                old.copy(
                    query = old.query.copy(query = value)
                )
            }
        }

        private fun handleFabClicked() {
            viewModelScope.launch {
                _navigation.send(InventoryNavigationTarget.ProductForm(null))
                _state.update { old -> old.copy(isScanBarcodeShowing = false) }
            }
        }

        fun onScanEvent(event: ScanEvent) {
            when (event) {
                ScanEvent.AddProductClicked -> handleAddProductClicked()
                is ScanEvent.BottomSheetDialogStateChanged ->
                    handleBottomSheetDialogStateChanged(
                        event.expanded
                    )
                is ScanEvent.CameraBoundaryReady -> handleCameraBoundaryReady(event.cameraBoundary)
                ScanEvent.Dismiss -> handleDismiss()
                is ScanEvent.ProductDetailClicked -> handleProductDetailClicked(event.productUUID)
                is ScanEvent.ScanningAreaReady -> handleScanningAreaReady(event.scanningArea)
                ScanEvent.BottomSheetDismiss -> handleBottomSheetDismiss()
            }
        }

        private fun handleBottomSheetDismiss() {
            _resultBottomSheetState.update { InventoryScanBottomSheetState.Hidden }
        }

        private fun handleAddProductClicked() {
            viewModelScope.launch {
                val productCode = barcodeResult.barCode.displayValue ?: return@launch
                _navigation.send(InventoryNavigationTarget.ProductForm(productCode))
                _state.update { old -> old.copy(isScanBarcodeShowing = false) }
            }
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

        private fun handleProductDetailClicked(productUUID: String) {
            viewModelScope.launch {
                _navigation.send(InventoryNavigationTarget.ProductDetail(productUUID))
                _state.update { old -> old.copy(isScanBarcodeShowing = false) }
            }
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
                InventoryScanBottomSheetState.Error.Generic
        }

        private suspend fun loadProductDetailsWithBarcodeResult(
            scanningResult: ScanningResult.PerfectMatch
        ) {
            val productCode = scanningResult.barCodeResult.barCode.displayValue
            if (productCode != null) {
                loadingProductCode = productCode
                showBottomSheetLoading(scanningResult.barCodeResult)
                freezeCameraPreview(true)
                val product = findProductByCode(productCode = productCode)
                if (product != null) {
                    bindBottomSheetResultInformation(
                        barcodeResult = scanningResult.barCodeResult,
                        informationModel =
                            InformationModel(
                                product = product
                            )
                    )
                } else {
                    bindBottomSheetResultAddProduct(
                        barcodeResult = scanningResult.barCodeResult
                    )
                }
            } else {
                // Show Error Information
            }
        }

        private suspend fun findProductByCode(productCode: String): Product? {
            val categories = _categories.firstOrNull() ?: return null
            var selectedProduct: Product? = null
            for (category in categories) {
                selectedProduct =
                    category.products.firstOrNull {
                            product ->
                        product.upc == productCode
                    }
                if (selectedProduct != null) {
                    break
                }
            }
            return selectedProduct
        }

        private fun bindBottomSheetResultInformation(
            barcodeResult: BarCodeResult,
            informationModel: InformationModel
        ) {
            InventoryScanBottomSheetState.ProductFound(
                barcodeResult = barcodeResult,
                information =
                    InformationModel(
                        product = informationModel.product
                    )
            ).also { _resultBottomSheetState.value = it }
            this.barcodeResult = barcodeResult
        }

        private fun showBottomSheetLoading(barcodeResult: BarCodeResult) {
            isResultBottomSheetShowing = true
            _resultBottomSheetState.value = InventoryScanBottomSheetState.Loading(barcodeResult)
            this.barcodeResult = barcodeResult
        }

        private fun bindBottomSheetResultAddProduct(barcodeResult: BarCodeResult) {
            InventoryScanBottomSheetState.AddProduct(
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