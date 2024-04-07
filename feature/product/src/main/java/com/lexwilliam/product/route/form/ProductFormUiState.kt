package com.lexwilliam.product.route.form

import com.lexwilliam.core.model.UploadImageFormat
import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.model.UiPriceAndQuantity

data class ProductFormUiState(
    val uuid: String? = null,
    val title: String = "",
    val sellPrice: String = "",
    val buyPriceList: Map<Int, UiPriceAndQuantity> = mapOf(),
    val selectedCategory: ProductCategory? = null,
    val description: String = "",
    val image: UploadImageFormat? = null,
    val takePhoto: Boolean = false,
    val isLoading: Boolean = false
)