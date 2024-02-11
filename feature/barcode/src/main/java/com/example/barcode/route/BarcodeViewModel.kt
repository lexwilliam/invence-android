package com.example.barcode.route

import android.graphics.RectF
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barcode.analyzer.BarcodeImageAnalyzer
import com.example.barcode.analyzer.BarcodeResultBoundaryAnalyzer
import com.example.barcode.model.BarCodeResult
import com.example.barcode.model.BarcodeScannerBottomSheetState
import com.example.barcode.model.InformationModel
import com.example.barcode.model.ScanningResult
import com.example.barcode.navigation.BarcodeNavigationTarget
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.lexwilliam.product.model.Product
import com.lexwilliam.product.usecase.InsertTempProductUseCase
import com.lexwilliam.product.usecase.ObserveProductCategoryUseCase
import com.lexwilliam.product.usecase.ObserveTempProductUseCase
import com.lexwilliam.user.usecase.FetchUserUseCase
import com.lexwilliam.user.usecase.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalGetImage
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BarcodeViewModel
    @Inject
    constructor(
        private val barcodeImageAnalyzer: BarcodeImageAnalyzer,
        private val barcodeResultBoundaryAnalyzer: BarcodeResultBoundaryAnalyzer,
        observeProductCategory: ObserveProductCategoryUseCase,
        private val observeTempProduct: ObserveTempProductUseCase,
        private val insertTempProduct: InsertTempProductUseCase,
        observeSession: ObserveSessionUseCase,
        fetchUser: FetchUserUseCase,
        savedStateHandle: SavedStateHandle
    ) : ViewModel(), DefaultLifecycleObserver {
        private val onlyID =
            savedStateHandle
                .get<String?>("onlyID")

        private val _scanningResult = MutableStateFlow<ScanningResult>(ScanningResult.Initial())
        val scanningResult = _scanningResult.asStateFlow()

        private val _navigation = Channel<BarcodeNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        private val _categories =
            observeSession().flatMapLatest { session ->
                val user = session.userUUID?.let { fetchUser(it) }
                user?.getOrNull()
                    ?.branchUUID
                    ?.let { uuid -> observeProductCategory(uuid) }
                    ?: flowOf(null)
            }

        private val _freezeCameraPreview = Channel<Boolean>()
        val freezeCameraPreview = _freezeCameraPreview.receiveAsFlow()

        private val _resultBottomSheetState =
            MutableStateFlow<BarcodeScannerBottomSheetState>(BarcodeScannerBottomSheetState.Hidden)
        val resultBottomSheetState =
            _resultBottomSheetState.asStateFlow()

        private var loadingProductCode: String = ""
        private var isResultBottomSheetShowing = false
        private lateinit var barcodeResult: BarCodeResult

        init {
            setupImageAnalyzer()
        }

        fun onBackButtonClicked() {
            viewModelScope.launch {
                if (isResultBottomSheetShowing) {
                    _resultBottomSheetState.value = BarcodeScannerBottomSheetState.Hidden
                } else {
                    _navigation.send(BarcodeNavigationTarget.BackStack)
                }
            }
        }

        fun onBarcodeScanningAreaReady(scanningArea: RectF) {
            barcodeResultBoundaryAnalyzer.onBarcodeScanningAreaReady(scanningArea)
        }

        fun onCameraBoundaryReady(cameraBoundary: RectF) {
            barcodeResultBoundaryAnalyzer.onCameraBoundaryReady(cameraBoundary)
        }

        fun getBarcodeImageAnalyzer(): BarcodeImageAnalyzer {
            return barcodeImageAnalyzer
        }

        fun onToolbarCloseIconClicked() {
            viewModelScope.launch {
                _navigation.send(BarcodeNavigationTarget.BackStack)
            }
        }

        fun onBottomSheetDialogStateChanged(expanded: Boolean) {
            isResultBottomSheetShowing =
                when {
                    expanded -> {
                        true
                    }
                    else -> {
                        // after bottom sheet hidden, resume camera preview
                        freezeCameraPreview(false)
                        false
                    }
                }
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
                BarcodeScannerBottomSheetState.Error.Generic
        }

        private suspend fun loadProductDetailsWithBarcodeResult(
            scanningResult: ScanningResult.PerfectMatch
        ) {
            val productCode = scanningResult.barCodeResult.barCode.displayValue
            if (productCode != null) {
                loadingProductCode = productCode
                showBottomSheetLoading(scanningResult.barCodeResult)
                freezeCameraPreview(true)
                if (onlyID != null) {
                    bindBottomSheetResultOnlyID(
                        barcodeResult = scanningResult.barCodeResult
                    )
                } else {
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
                        product.uuid == productCode
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
            BarcodeScannerBottomSheetState.ProductFound(
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
            _resultBottomSheetState.value = BarcodeScannerBottomSheetState.Loading(barcodeResult)
            this.barcodeResult = barcodeResult
        }

        private fun bindBottomSheetResultAddProduct(barcodeResult: BarCodeResult) {
            BarcodeScannerBottomSheetState.AddProduct(
                barcodeResult = barcodeResult
            ).also { _resultBottomSheetState.value = it }
            this.barcodeResult = barcodeResult
        }

        private fun bindBottomSheetResultOnlyID(barcodeResult: BarCodeResult) {
            BarcodeScannerBottomSheetState.OnlyID(
                barcodeResult = barcodeResult
            ).also { _resultBottomSheetState.value = it }
            this.barcodeResult = barcodeResult
        }

        fun handleAddProductClicked() {
            viewModelScope.launch {
                val productCode = barcodeResult.barCode.displayValue ?: return@launch
                _navigation.send(BarcodeNavigationTarget.ProductForm(productCode, false))
            }
        }

        fun handleToProductDetailClicked() {
            viewModelScope.launch {
                val productCode = barcodeResult.barCode.displayValue ?: return@launch
                _navigation.send(BarcodeNavigationTarget.ProductDetail(productCode))
            }
        }

        fun handleOnlyIDConfirm() {
            viewModelScope.launch {
                val productCode = barcodeResult.barCode.displayValue ?: return@launch
                val product = observeTempProduct().firstOrNull() ?: return@launch
                insertTempProduct(product.copy(uuid = productCode))
                _navigation.send(BarcodeNavigationTarget.ProductForm(null, true))
            }
        }

        private fun freezeCameraPreview(freeze: Boolean) {
            // true - freeze the camera preview,
            // false - resume the camera preview
            viewModelScope.launch {
                _freezeCameraPreview.send(freeze)
            }
        }
    }