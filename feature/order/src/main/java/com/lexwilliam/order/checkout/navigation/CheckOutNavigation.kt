package com.lexwilliam.order.checkout.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.order.checkout.route.CheckOutRoute
import java.util.UUID

fun NavGraphBuilder.checkOutNavigation(
    onBackStack: () -> Unit,
    toCart: () -> Unit
) {
    composable(
        route = "${Screen.CHECK_OUT}?orderUUID={orderUUID}",
        arguments =
            listOf(
                navArgument("orderUUID") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
    ) {
        CheckOutRoute(
            onBackStack = onBackStack,
            toCart = toCart
        )
    }
}

fun NavController.navigateToCheckOut(
    orderUUID: UUID,
    options: NavOptions? = null
) {
    this.navigate("${Screen.CHECK_OUT}?orderUUID=$orderUUID", options)
}