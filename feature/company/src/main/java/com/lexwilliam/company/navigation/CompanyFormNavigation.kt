package com.lexwilliam.company.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lexwilliam.company.route.form.CompanyFormRoute
import com.lexwilliam.core.navigation.Screen

fun NavGraphBuilder.companyFormNavigation(toHome: () -> Unit) {
    composable(
        route = Screen.COMPANY_FORM
    ) {
        CompanyFormRoute(
            toHome = toHome
        )
    }
}

fun NavController.navigateToCompanyForm(options: NavOptions? = null) {
    this.navigate(Screen.COMPANY_FORM, options)
}