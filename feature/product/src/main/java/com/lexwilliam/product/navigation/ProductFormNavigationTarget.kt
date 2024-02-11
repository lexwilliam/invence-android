package com.lexwilliam.product.navigation

sealed interface ProductFormNavigationTarget {
    data object BackStack : ProductFormNavigationTarget

    data object Inventory : ProductFormNavigationTarget

    data class Barcode(val onlyID: String) : ProductFormNavigationTarget
}