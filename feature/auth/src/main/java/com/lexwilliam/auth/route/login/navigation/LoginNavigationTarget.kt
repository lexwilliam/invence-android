package com.lexwilliam.auth.route.login.navigation

sealed interface LoginNavigationTarget {
    data object CompanySearch : LoginNavigationTarget

    data object Home : LoginNavigationTarget

    data object SignUp : LoginNavigationTarget

    data object ForgotPassword : LoginNavigationTarget
}