package com.lexwilliam.product.model

import com.lexwilliam.core.model.UploadImageFormat
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class ProductWithImageFormat(
    val uuid: String = "",
    val name: String = "",
    val description: String = "",
    val categoryName: String = "",
    val sellPrice: Double = 0.0,
    val items: List<ProductItem> = emptyList(),
    val imagePath: UploadImageFormat? = null,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant? = null
)