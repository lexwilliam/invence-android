package com.lexwilliam.inventory.route

import com.lexwilliam.product.util.ProductQueryStrategy

data class InventoryUiState(
    val query: ProductQueryStrategy = ProductQueryStrategy(),
    val isScanBarcodeShowing: Boolean = false
)