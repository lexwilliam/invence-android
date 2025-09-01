package com.lexwilliam.transaction.history.navigation

import java.util.UUID

sealed interface TransactionHistoryNavigationTarget {
    data class TransactionDetail(val uuid: UUID) : TransactionHistoryNavigationTarget
}