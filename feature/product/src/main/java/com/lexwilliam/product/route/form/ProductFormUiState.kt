package com.lexwilliam.product.route.form

import android.net.Uri
import com.lexwilliam.product.model.UiCategory

data class ProductFormUiState(
    val title: String = "",
    val buyPrice: String = "",
    val sellPrice: String = "",
    val quantity: Int = 0,
    val selectedCategory: UiCategory? = null,
    val description: String = "",
    val imagePath: Uri? = null
)