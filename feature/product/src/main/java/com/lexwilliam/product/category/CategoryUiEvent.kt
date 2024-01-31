package com.lexwilliam.product.category

import android.net.Uri
import com.lexwilliam.product.model.ProductCategory

sealed interface CategoryUiEvent {
    data class QueryChanged(val value: String) : CategoryUiEvent

    data class CategoryClicked(val item: ProductCategory) : CategoryUiEvent

    data class InputImageChanged(val uri: Uri?) : CategoryUiEvent

    data class AddCategoryTitleChanged(val value: String) : CategoryUiEvent

    data object AddCategoryConfirm : CategoryUiEvent

    data object Dismiss : CategoryUiEvent
}