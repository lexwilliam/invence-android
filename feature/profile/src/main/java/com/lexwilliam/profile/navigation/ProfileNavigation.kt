package com.lexwilliam.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.profile.route.ProfileRoute

fun NavGraphBuilder.profileNavigation(
    onBackStack: () -> Unit,
    toLogin: () -> Unit
) {
    composable(
        route = Screen.PROFILE
    ) {
        ProfileRoute(
            onBackStack = onBackStack,
            toLogin = toLogin
        )
    }
}

fun NavController.navigateToProfile(options: NavOptions? = null) {
    this.navigate(Screen.PROFILE, options)
}