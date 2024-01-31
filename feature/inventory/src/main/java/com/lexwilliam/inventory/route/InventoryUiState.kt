package com.lexwilliam.inventory.route

import com.lexwilliam.core.util.QueryStrategy
import com.lexwilliam.product.model.ProductCategory

data class InventoryUiState(
    val queryStrategy: QueryStrategy = QueryStrategy(),
    val productCategories: List<ProductCategory> = emptyList()
)