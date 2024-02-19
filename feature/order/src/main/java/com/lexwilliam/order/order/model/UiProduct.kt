package com.lexwilliam.order.order.model

import com.lexwilliam.product.model.Product
import com.lexwilliam.product.model.ProductCategory

data class UiProduct(
    val category: ProductCategory,
    val product: Product
)