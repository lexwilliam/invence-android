package com.lexwilliam.inventory.navigation

import java.util.UUID

sealed interface InventoryNavigationTarget {
    data class ProductForm(val productUUID: UUID?) : InventoryNavigationTarget
}