package com.lexwilliam.product.model

import android.net.Uri
import kotlinx.datetime.Instant
import java.util.UUID

data class ProductCategory(
    val uuid: UUID = UUID.randomUUID(),
    val branchUUID: UUID = UUID.randomUUID(),
    val name: String = "",
    val imageUrl: Uri? = null,
    val products: List<Product> = emptyList(),
    val createdAt: Instant = Instant.DISTANT_PAST,
    val deletedAt: Instant? = null
)