package com.lexwilliam.company.navigation

sealed interface CompanySearchNavigationTarget {
    data object CompanyForm : CompanySearchNavigationTarget
}