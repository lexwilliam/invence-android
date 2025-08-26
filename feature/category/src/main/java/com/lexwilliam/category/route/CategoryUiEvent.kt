package com.lexwilliam.category.route

import com.lexwilliam.product.model.ProductCategory

sealed interface CategoryUiEvent {
    data class QueryChanged(val value: String) : CategoryUiEvent

    data class OpenForm(val category: ProductCategory?) : CategoryUiEvent

    data class DeleteCategory(val category: ProductCategory) : CategoryUiEvent

    data object DismissForm : CategoryUiEvent

    data class ConfirmForm(
        val category: ProductCategory?,
        val name: String
    ) : CategoryUiEvent
}