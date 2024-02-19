package com.lexwilliam.order.cart.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.order.cart.route.CartRoute
import java.util.UUID

fun NavGraphBuilder.cartNavigation(toOrder: (UUID) -> Unit) {
    composable(route = Screen.CART) {
        CartRoute(
            toOrder = toOrder
        )
    }
}

fun NavController.navigateToCart(options: NavOptions? = null) {
    this.navigate(Screen.CART, options)
}