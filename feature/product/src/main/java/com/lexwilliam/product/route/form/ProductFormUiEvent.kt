package com.lexwilliam.product.route.form

import android.graphics.Bitmap
import android.net.Uri

sealed interface ProductFormUiEvent {
    data object BackStackClicked : ProductFormUiEvent

    data class InputImageChanged(val uri: Uri?) : ProductFormUiEvent

    data object CameraClicked : ProductFormUiEvent

    data class ProductPhotoTaken(val bitmap: Bitmap) : ProductFormUiEvent

    data object AddProductItem : ProductFormUiEvent

    data class RemoveProductItem(val itemId: Int) : ProductFormUiEvent

    data object ScanBarcodeClicked : ProductFormUiEvent

    data class TitleValueChanged(val value: String) : ProductFormUiEvent

    data class BuyPriceValueChanged(val itemId: Int, val value: String) : ProductFormUiEvent

    data class SellPriceValueChanged(val value: String) : ProductFormUiEvent

    data class QuantityValueChanged(val itemId: Int, val value: String) : ProductFormUiEvent

    data object SelectCategoryClicked : ProductFormUiEvent

    data class DescriptionValueChanged(val value: String) : ProductFormUiEvent

    data object SaveClicked : ProductFormUiEvent
}