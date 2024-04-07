package com.lexwilliam.company.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lexwilliam.company.route.search.CompanySearchRoute
import com.lexwilliam.core.navigation.Screen

fun NavGraphBuilder.companySearchNavigation(
    toCompanyForm: () -> Unit,
    toHome: () -> Unit
) {
    composable(
        route = Screen.COMPANY_SEARCH
    ) {
        CompanySearchRoute(
            toCompanyForm = toCompanyForm,
            toHome = toHome
        )
    }
}

fun NavController.navigateToCompanySearch(options: NavOptions? = null) {
    this.navigate(Screen.COMPANY_SEARCH, options)
}