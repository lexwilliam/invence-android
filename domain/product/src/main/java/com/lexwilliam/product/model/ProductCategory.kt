package com.lexwilliam.product.model

import kotlinx.datetime.Instant
import java.util.UUID

data class ProductCategory(
    val uuid: UUID = UUID.randomUUID(),
    val userUUID: String,
    val name: String = "",
    val products: List<Product> = emptyList(),
    val createdAt: Instant = Instant.DISTANT_PAST,
    val deletedAt: Instant? = null
)