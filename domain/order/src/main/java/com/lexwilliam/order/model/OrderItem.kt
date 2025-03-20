package com.lexwilliam.order.model

import android.net.Uri

data class OrderItem(
    val uuid: String,
    val upc: String?,
    val name: String,
    val categoryName: String,
    val label: String,
    val price: Double,
    val imagePath: Uri?,
    val description: String
)