package com.example.barcode.model

sealed class BarcodeScannerBottomSheetState {
    data class Loading(
        val barcodeResult: BarCodeResult
    ) : BarcodeScannerBottomSheetState()

    data class ProductFound(
        val barcodeResult: BarCodeResult,
        val information: InformationModel
    ) : BarcodeScannerBottomSheetState()

    data class AddProduct(
        val barcodeResult: BarCodeResult
    ) : BarcodeScannerBottomSheetState()

    data class OnlyID(
        val barcodeResult: BarCodeResult
    ) : BarcodeScannerBottomSheetState()

    sealed class Error : BarcodeScannerBottomSheetState() {
        object Generic : Error()
    }

    object Hidden : BarcodeScannerBottomSheetState()
}