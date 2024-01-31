package com.lexwilliam.product.route.form

import android.net.Uri

sealed interface ProductFormUiEvent {
    data object BackStackClicked : ProductFormUiEvent

    data class InputImageChanged(val uri: Uri?) : ProductFormUiEvent

    data object ScanBarcodeClicked : ProductFormUiEvent

    data class TitleValueChanged(val value: String) : ProductFormUiEvent

    data class BuyPriceValueChanged(val value: String) : ProductFormUiEvent

    data class SellPriceValueChanged(val value: String) : ProductFormUiEvent

    data class QuantityValueChanged(val value: String) : ProductFormUiEvent

    data object SelectCategoryClicked : ProductFormUiEvent

    data class DescriptionValueChanged(val value: String) : ProductFormUiEvent

    data object SaveClicked : ProductFormUiEvent
}