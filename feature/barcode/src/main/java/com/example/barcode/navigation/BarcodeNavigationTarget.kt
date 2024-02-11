package com.example.barcode.navigation

sealed interface BarcodeNavigationTarget {
    data object BackStack : BarcodeNavigationTarget

    data class ProductForm(val productUUID: String?, val onlyID: Boolean) : BarcodeNavigationTarget

    data class ProductDetail(val productUUID: String) : BarcodeNavigationTarget
}