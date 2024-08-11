package com.lexwilliam.auth.navigation

import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lexwilliam.auth.route.login.LoginRoute
import com.lexwilliam.core.navigation.Screen

fun NavGraphBuilder.loginNavigation(
    toCompanySearch: () -> Unit,
    toHome: () -> Unit
) {
    composable(route = Screen.LOGIN) {
        LoginRoute(
            toCompanySearch = toCompanySearch,
            toHome = toHome
        )
    }
}

fun NavController.navigateToLogin(options: NavOptions? = null) {
    this.navigate(Screen.LOGIN, options)
}