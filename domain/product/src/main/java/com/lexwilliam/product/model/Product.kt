package com.lexwilliam.product.model

import android.net.Uri
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Product(
    val uuid: String = "",
    val name: String = "",
    val description: String = "",
    val categoryName: String = "",
    val sellPrice: Double = 0.0,
    val items: List<ProductItem> = emptyList(),
    val imagePath: Uri? = null,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant? = null
)