package com.lexwilliam.order.order.model

import com.lexwilliam.product.model.Product

data class UiCartItem(
    val product: Product,
    val quantity: Int
)