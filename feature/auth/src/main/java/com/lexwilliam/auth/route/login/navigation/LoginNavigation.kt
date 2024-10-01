package com.lexwilliam.auth.route.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lexwilliam.auth.route.login.route.LoginRoute
import com.lexwilliam.core.navigation.Screen

fun NavGraphBuilder.loginNavigation(
    toCompanySearch: () -> Unit,
    toHome: () -> Unit,
    toForgotPassword: () -> Unit,
    toSignUp: () -> Unit
) {
    composable(route = Screen.LOGIN) {
        LoginRoute(
            toCompanySearch = toCompanySearch,
            toHome = toHome,
            toForgotPassword = toForgotPassword,
            toSignUp = toSignUp
        )
    }
}

fun NavController.navigateToLogin(options: NavOptions? = null) {
    this.navigate(Screen.LOGIN, options)
}