package com.lexwilliam.inventory.route

sealed interface InventoryUiEvent {
    data class QueryChanged(val value: String) : InventoryUiEvent

    object FabClicked : InventoryUiEvent
}