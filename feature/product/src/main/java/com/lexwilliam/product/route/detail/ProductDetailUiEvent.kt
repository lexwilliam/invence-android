package com.lexwilliam.product.route.detail

sealed interface ProductDetailUiEvent {
    data object BackStackClicked : ProductDetailUiEvent

    data object RestockClicked : ProductDetailUiEvent

    data object ItemExpanded : ProductDetailUiEvent

    data object CopyDescription : ProductDetailUiEvent

    data object EditIconClicked : ProductDetailUiEvent

    data object DeleteIconClicked : ProductDetailUiEvent
}