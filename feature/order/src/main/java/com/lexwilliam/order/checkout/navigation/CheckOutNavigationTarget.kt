package com.lexwilliam.order.checkout.navigation

sealed interface CheckOutNavigationTarget {
    data object BackStack : CheckOutNavigationTarget

    data object Cart : CheckOutNavigationTarget
}