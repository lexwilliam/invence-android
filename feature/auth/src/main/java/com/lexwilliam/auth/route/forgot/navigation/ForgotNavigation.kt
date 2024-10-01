package com.lexwilliam.auth.route.forgot.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lexwilliam.auth.route.forgot.route.ForgotRoute
import com.lexwilliam.core.navigation.Screen

fun NavGraphBuilder.forgotNavigation(onBackStack: () -> Unit) {
    composable(route = Screen.FORGOT_PASSWORD) {
        ForgotRoute(
            onBackStack = onBackStack
        )
    }
}

fun NavController.navigateToForgotPassword(options: NavOptions? = null) {
    this.navigate(Screen.FORGOT_PASSWORD, options)
}