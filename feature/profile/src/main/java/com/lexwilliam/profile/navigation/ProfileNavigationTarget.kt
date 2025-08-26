package com.lexwilliam.profile.navigation

sealed interface ProfileNavigationTarget {
    data object Login : ProfileNavigationTarget
}