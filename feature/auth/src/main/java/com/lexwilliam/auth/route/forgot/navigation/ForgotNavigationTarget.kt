package com.lexwilliam.auth.route.forgot.navigation

sealed interface ForgotNavigationTarget {
    data object BackStack : ForgotNavigationTarget
}