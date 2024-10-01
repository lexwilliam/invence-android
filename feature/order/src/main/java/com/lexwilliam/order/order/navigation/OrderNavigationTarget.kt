package com.lexwilliam.order.order.navigation

import java.util.UUID

sealed interface OrderNavigationTarget {
    data object BackStack : OrderNavigationTarget

    data class CheckOut(val orderUUID: UUID) : OrderNavigationTarget
}