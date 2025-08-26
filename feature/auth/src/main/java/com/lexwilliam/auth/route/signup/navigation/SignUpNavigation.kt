package com.lexwilliam.auth.route.signup.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lexwilliam.auth.route.signup.route.SignUpRoute
import com.lexwilliam.core.navigation.Screen

fun NavGraphBuilder.signUpNavigation(
    onBackStack: () -> Unit,
    toHome: () -> Unit
) {
    composable(route = Screen.SIGN_UP) {
        SignUpRoute(
            onBackStack = onBackStack,
            toHome = toHome
        )
    }
}

fun NavController.navigateToSignUp(options: NavOptions? = null) {
    this.navigate(Screen.SIGN_UP, options)
}