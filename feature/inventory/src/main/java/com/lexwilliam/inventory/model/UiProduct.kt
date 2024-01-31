package com.lexwilliam.inventory.model

import com.lexwilliam.product.model.Product
import com.lexwilliam.product.model.ProductCategory

data class UiProduct(
    val category: ProductCategory,
    val product: Product
)