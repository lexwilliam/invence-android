package com.lexwilliam.auth.route.signup.navigation

sealed interface SignUpNavigationTarget {
    data object BackStack : SignUpNavigationTarget

    data object CompanySearch : SignUpNavigationTarget
}