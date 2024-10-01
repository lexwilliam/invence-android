package com.lexwilliam.inventory.route

import com.lexwilliam.product.model.Product
import com.lexwilliam.product.model.ProductCategory

sealed interface InventoryUiEvent {
    data object BackStackClicked : InventoryUiEvent

    data class QueryChanged(val value: String) : InventoryUiEvent

    data object FabClicked : InventoryUiEvent

    data class ProductClicked(val product: Product) : InventoryUiEvent

    data object BarcodeScannerClicked : InventoryUiEvent

    data class CategoryClicked(val category: ProductCategory?) : InventoryUiEvent

    data object CategorySettingClicked : InventoryUiEvent
}