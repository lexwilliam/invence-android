package com.lexwilliam.company.navigation

sealed interface CompanySearchNavigationTarget {
    data object CompanyForm : CompanySearchNavigationTarget

    data object Home : CompanySearchNavigationTarget
}