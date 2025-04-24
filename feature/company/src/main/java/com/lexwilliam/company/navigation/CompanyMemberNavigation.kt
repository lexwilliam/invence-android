package com.lexwilliam.company.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lexwilliam.company.route.member.CompanyMemberRoute
import com.lexwilliam.core.navigation.Screen

fun NavGraphBuilder.companyMemberNavigation(onBackStack: () -> Unit) {
    composable(
        route = Screen.COMPANY_MEMBER
    ) {
        CompanyMemberRoute(
            onBackStack = onBackStack
        )
    }
}

fun NavController.navigateToCompanyMember(options: NavOptions? = null) {
    this.navigate(Screen.COMPANY_MEMBER, options)
}