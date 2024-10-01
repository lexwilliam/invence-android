package com.lexwilliam.inventory.navigation

sealed interface InventoryNavigationTarget {
    data object BackStack : InventoryNavigationTarget

    data class ProductForm(val productUUID: String?) : InventoryNavigationTarget

    data class ProductDetail(val productUUID: String) : InventoryNavigationTarget

    data object Category : InventoryNavigationTarget
}