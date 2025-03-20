package com.lexwilliam.product.route.form

import com.lexwilliam.product.model.ProductCategory

sealed interface ProductFormUiEvent {
    data object BackStackClicked : ProductFormUiEvent

    data class InputImageChanged(val image: Any?) : ProductFormUiEvent

    data object CameraClicked : ProductFormUiEvent

    data object AddProductItem : ProductFormUiEvent

    data class RemoveProductItem(val itemId: Int) : ProductFormUiEvent

    data object ScanBarcodeClicked : ProductFormUiEvent

    data class SkuChanged(val value: String) : ProductFormUiEvent

    data class UpcChanged(val value: String) : ProductFormUiEvent

    data class TitleValueChanged(val value: String) : ProductFormUiEvent

    data class BuyPriceValueChanged(val itemId: Int, val value: String) : ProductFormUiEvent

    data class SellPriceValueChanged(val value: String) : ProductFormUiEvent

    data class QuantityValueChanged(val itemId: Int, val value: String) : ProductFormUiEvent

    data object SelectCategoryClicked : ProductFormUiEvent

    data class AddCategory(val name: String, val image: Any?) : ProductFormUiEvent

    data class CategorySelected(val category: ProductCategory) : ProductFormUiEvent

    data object CategoryDismiss : ProductFormUiEvent

    data class DescriptionValueChanged(val value: String) : ProductFormUiEvent

    data object SaveClicked : ProductFormUiEvent

    data object GenerateSkuClicked : ProductFormUiEvent
}