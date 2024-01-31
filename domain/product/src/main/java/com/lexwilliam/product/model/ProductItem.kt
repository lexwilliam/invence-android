package com.lexwilliam.product.model

import kotlinx.datetime.Instant

data class ProductItem(
    val quantity: Int,
    val buyPrice: Double,
    val createdAt: Instant,
    val deletedAt: Instant?
)