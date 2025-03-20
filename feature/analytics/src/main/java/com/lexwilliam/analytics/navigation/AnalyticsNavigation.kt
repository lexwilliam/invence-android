package com.lexwilliam.analytics.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lexwilliam.analytics.route.AnalyticsRoute
import com.lexwilliam.core.navigation.Screen

fun NavGraphBuilder.analyticsNavigation(onDrawerNavigation: (String) -> Unit) {
    composable(route = Screen.ANALYTICS) {
        AnalyticsRoute(
            onDrawerNavigation = onDrawerNavigation
        )
    }
}

fun NavController.navigateToAnalytics(options: NavOptions? = null) {
    this.navigate(Screen.ANALYTICS, options)
}