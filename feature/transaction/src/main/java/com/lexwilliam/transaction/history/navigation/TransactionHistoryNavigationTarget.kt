package com.lexwilliam.transaction.history.navigation

sealed interface TransactionHistoryNavigationTarget {
    data object BackStack : TransactionHistoryNavigationTarget
}