package com.lexwilliam.transaction.detail.navigation

sealed interface TransactionDetailNavigationTarget {
    data object BackStackClicked : TransactionDetailNavigationTarget
}