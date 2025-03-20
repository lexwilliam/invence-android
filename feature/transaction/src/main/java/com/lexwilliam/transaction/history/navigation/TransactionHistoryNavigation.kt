package com.lexwilliam.transaction.history.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.transaction.history.route.TransactionHistoryRoute

fun NavGraphBuilder.transactionHistoryNavigation(onDrawerNavigation: (String) -> Unit) {
    composable(
        route = Screen.TRANSACTION_HISTORY
    ) {
        TransactionHistoryRoute(
            onDrawerNavigation = onDrawerNavigation
        )
    }
}

fun NavController.navigateToTransactionHistory(options: NavOptions? = null) {
    this.navigate(Screen.TRANSACTION_HISTORY, options)
}