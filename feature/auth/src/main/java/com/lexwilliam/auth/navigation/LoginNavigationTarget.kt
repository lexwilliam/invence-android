package com.lexwilliam.auth.navigation

sealed interface LoginNavigationTarget {
    data object CompanySearch : LoginNavigationTarget

    data object Home : LoginNavigationTarget
}