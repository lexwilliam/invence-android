package com.lexwilliam.order.order.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.order.order.route.OrderRoute
import java.util.UUID

fun NavGraphBuilder.orderNavigation(
    toCheckOut: (UUID) -> Unit,
    onBackStack: () -> Unit
) {
    composable(
        route = "${Screen.ORDER}?orderUUID={orderUUID}",
        arguments =
            listOf(
                navArgument("orderUUID") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
    ) {
        OrderRoute(
            onBackStack = onBackStack,
            toCheckOut = toCheckOut
        )
    }
}

fun NavController.navigateToOrder(
    orderUUID: UUID,
    options: NavOptions? = null
) {
    this.navigate("${Screen.ORDER}?orderUUID=$orderUUID", options)
}