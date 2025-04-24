package com.lexwilliam.company.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lexwilliam.company.route.my.MyCompanyRoute
import com.lexwilliam.core.navigation.Screen

fun NavGraphBuilder.myCompanyNavigation(
    onBackStack: () -> Unit,
    toCompanyMember: () -> Unit
) {
    composable(
        route = Screen.MY_COMPANY
    ) {
        MyCompanyRoute(
            onBackStack = onBackStack,
            toCompanyMember = toCompanyMember
        )
    }
}

fun NavController.navigateToMyCompany(options: NavOptions? = null) {
    this.navigate(Screen.MY_COMPANY, options)
}