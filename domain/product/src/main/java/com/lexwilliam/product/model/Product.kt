package com.lexwilliam.product.model

import android.net.Uri
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID

data class Product(
    val uuid: UUID = UUID.randomUUID(),
    val name: String = "",
    val description: String = "",
    val categoryName: String = "",
    val sellPrice: Double = 0.0,
    val items: List<ProductItem> = emptyList(),
    val imagePath: Uri? = null,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant? = null
)