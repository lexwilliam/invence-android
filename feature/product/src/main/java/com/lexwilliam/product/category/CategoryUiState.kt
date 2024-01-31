package com.lexwilliam.product.category

import android.net.Uri
import com.lexwilliam.product.model.ProductCategory

data class CategoryUiState(
    val query: String = "",
    val categories: List<ProductCategory> = emptyList(),
    val formTitle: String = "",
    val formImagePath: Uri? = null
)