package com.lexwilliam.product.route.form

import android.net.Uri
import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.model.UiPriceAndQuantity

data class ProductFormUiState(
    val sku: String = "",
    val upc: String? = null,
    val title: String = "",
    val sellPrice: String = "",
    val buyPriceList: Map<Int, UiPriceAndQuantity> = mapOf(),
    val selectedCategory: ProductCategory? = null,
    val description: String = "",
    val image: Uri? = null,
    val takePhoto: Boolean = false,
    val isLoading: Boolean = false,
    val isScanBarcodeShowing: Boolean = false,
    val isSelectCategoryShowing: Boolean = false
) {
    val isValid =
        sku.isNotBlank() &&
            title.isNotBlank() &&
            sellPrice.isNotBlank() &&
            selectedCategory != null
}