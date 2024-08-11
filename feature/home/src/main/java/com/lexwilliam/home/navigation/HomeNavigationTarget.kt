package com.lexwilliam.home.navigation

import java.util.UUID

sealed interface HomeNavigationTarget {
    data object Inventory : HomeNavigationTarget

    data object Cart : HomeNavigationTarget

    data object TransactionHistory : HomeNavigationTarget

    data class TransactionDetail(val transactionUUID: UUID) : HomeNavigationTarget

    data object Profile : HomeNavigationTarget
}