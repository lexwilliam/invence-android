package com.lexwilliam.product.navigation

sealed interface ProductFormNavigationTarget {
    data object BackStackClicked : ProductFormNavigationTarget
}