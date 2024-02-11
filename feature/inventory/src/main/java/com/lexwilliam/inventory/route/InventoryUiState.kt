package com.lexwilliam.inventory.route

import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.util.ProductQueryStrategy

data class InventoryUiState(
    val query: ProductQueryStrategy = ProductQueryStrategy(),
    val productCategories: List<ProductCategory> = emptyList()
)