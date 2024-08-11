package com.lexwilliam.profile.navigation

sealed interface ProfileNavigationTarget {
    data object BackStack : ProfileNavigationTarget

    data object Login : ProfileNavigationTarget
}