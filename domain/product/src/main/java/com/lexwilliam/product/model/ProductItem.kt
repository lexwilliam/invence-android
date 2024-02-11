package com.lexwilliam.product.model

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.datetime.Instant

@RequiresApi(Build.VERSION_CODES.O)
data class ProductItem(
    val itemId: Int,
    val quantity: Int,
    val buyPrice: Double,
    val createdAt: Instant,
    val deletedAt: Instant? = null
)