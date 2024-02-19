package com.lexwilliam.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.home.route.HomeRoute

fun NavGraphBuilder.homeNavigation(
    toInventory: () -> Unit,
    toCart: () -> Unit
) {
    composable(route = Screen.HOME) {
        HomeRoute(
            toInventory = toInventory,
            toCart = toCart
        )
    }
}

fun NavController.navigateToHome(options: NavOptions? = null) {
    this.navigate(Screen.HOME, options)
}