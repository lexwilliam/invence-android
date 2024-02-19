package com.lexwilliam.order.order.route

import com.lexwilliam.product.util.ProductQueryStrategy

data class OrderUiState(
    val query: ProductQueryStrategy = ProductQueryStrategy(),
    val isLoading: Boolean = false
)