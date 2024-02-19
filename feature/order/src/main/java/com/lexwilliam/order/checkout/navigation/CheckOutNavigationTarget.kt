package com.lexwilliam.order.checkout.navigation

import java.util.UUID

sealed interface CheckOutNavigationTarget {
    data object BackStack : CheckOutNavigationTarget

    data object Cart : CheckOutNavigationTarget

    data class TransactionDetail(val uuid: UUID): CheckOutNavigationTarget
}