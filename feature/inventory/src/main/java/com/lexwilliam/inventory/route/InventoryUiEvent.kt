package com.lexwilliam.inventory.route

import com.lexwilliam.product.model.Product

sealed interface InventoryUiEvent {
    data class QueryChanged(val value: String) : InventoryUiEvent

    data object FabClicked : InventoryUiEvent

    data class ProductClicked(val product: Product) : InventoryUiEvent

    data object BarcodeScannerClicked : InventoryUiEvent
}