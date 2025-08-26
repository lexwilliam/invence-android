package com.lexwilliam.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.profile.route.ProfileRoute

fun NavGraphBuilder.profileNavigation(
    toLogin: () -> Unit,
    onDrawerNavigation: (String) -> Unit
) {
    composable(
        route = Screen.PROFILE
    ) {
        ProfileRoute(
            toLogin = toLogin,
            onDrawerNavigation = onDrawerNavigation
        )
    }
}

fun NavController.navigateToProfile(options: NavOptions? = null) {
    this.navigate(Screen.PROFILE, options)
}