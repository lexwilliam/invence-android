package com.lexwilliam.order.cart.navigation

import java.util.UUID

sealed interface CartNavigationTarget {
    data object BackStack : CartNavigationTarget

    data class Order(val uuid: UUID) : CartNavigationTarget
}