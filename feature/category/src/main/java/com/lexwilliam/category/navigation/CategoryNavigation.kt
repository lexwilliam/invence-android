package com.lexwilliam.category.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lexwilliam.category.route.CategoryRoute
import com.lexwilliam.core.navigation.Screen

fun NavGraphBuilder.categoryNavigation(onBackStack: () -> Unit) {
    composable(route = Screen.CATEGORY) {
        CategoryRoute(
            onBackStack = onBackStack
        )
    }
}

fun NavController.navigateToCategory(options: NavOptions? = null) {
    this.navigate(Screen.CATEGORY, options)
}