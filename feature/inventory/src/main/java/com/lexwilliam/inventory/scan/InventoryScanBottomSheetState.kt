package com.lexwilliam.inventory.scan

import com.example.barcode.model.BarCodeResult
import com.example.barcode.model.InformationModel

sealed class InventoryScanBottomSheetState {
    data class Loading(
        val barcodeResult: BarCodeResult
    ) : InventoryScanBottomSheetState()

    data class ProductFound(
        val barcodeResult: BarCodeResult,
        val information: InformationModel
    ) : InventoryScanBottomSheetState()

    data class AddProduct(
        val barcodeResult: BarCodeResult
    ) : InventoryScanBottomSheetState()

    sealed class Error : InventoryScanBottomSheetState() {
        object Generic : Error()
    }

    object Hidden : InventoryScanBottomSheetState()
}