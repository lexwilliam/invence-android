package com.lexwilliam.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.lexwilliam.auth.route.login.LoginRoute
import com.lexwilliam.core.navigation.Screen

fun NavGraphBuilder.loginNavigation() {
    composable(route = Screen.LOGIN) {
        LoginRoute()
    }
}
