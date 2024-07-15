package com.lexwilliam.product.route.form.scan

import com.example.barcode.model.BarCodeResult

sealed class ProductScanBottomSheetState {
    data class Loading(
        val barcodeResult: BarCodeResult
    ) : ProductScanBottomSheetState()

    data class ScanResult(
        val barcodeResult: BarCodeResult
    ) : ProductScanBottomSheetState()

    sealed class Error : ProductScanBottomSheetState() {
        object Generic : Error()
    }

    object Hidden : ProductScanBottomSheetState()
}