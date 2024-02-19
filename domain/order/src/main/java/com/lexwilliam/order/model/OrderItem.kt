package com.lexwilliam.order.model

import android.net.Uri

data class OrderItem(
    val uuid: String = "",
    val name: String = "",
    val categoryName: String = "",
    val label: String = "",
    val price: Double = 0.0,
    val imagePath: Uri? = null,
    val description: String = ""
)