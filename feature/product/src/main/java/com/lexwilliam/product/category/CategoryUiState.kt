package com.lexwilliam.product.category

import com.lexwilliam.core.model.UploadImageFormat
import com.lexwilliam.product.model.ProductCategory

data class CategoryUiState(
    val query: String = "",
    val categories: List<ProductCategory> = emptyList(),
    val isFormShown: Boolean = false,
    val formTitle: String = "",
    val formImagePath: UploadImageFormat? = null,
    val takePhoto: Boolean = false
)