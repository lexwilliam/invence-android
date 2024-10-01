package com.lexwilliam.category.route

import com.lexwilliam.product.model.ProductCategory

data class CategoryUiState(
    val query: String = "",
    val isEditing: Boolean = false,
    val selectedCategory: ProductCategory? = null,
    val shouldNavigateBack: Boolean = false
)