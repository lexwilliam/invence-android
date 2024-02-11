package com.lexwilliam.product.navigation

sealed interface ProductDetailNavigationTarget {
    data object BackStack : ProductDetailNavigationTarget

    data class ProductForm(val productUUID: String) : ProductDetailNavigationTarget
}