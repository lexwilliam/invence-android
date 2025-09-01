package com.lexwilliam.order.order.navigation

sealed interface OrderNavigationTarget {
    data object BackStack : OrderNavigationTarget
}