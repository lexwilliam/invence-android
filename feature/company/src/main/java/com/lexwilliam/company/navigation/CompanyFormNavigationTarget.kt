package com.lexwilliam.company.navigation

sealed interface CompanyFormNavigationTarget {
    data object Home : CompanyFormNavigationTarget
}