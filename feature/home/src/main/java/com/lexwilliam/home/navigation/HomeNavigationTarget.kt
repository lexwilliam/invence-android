package com.lexwilliam.home.navigation

sealed interface HomeNavigationTarget {
    data object Inventory : HomeNavigationTarget

    data object Cart : HomeNavigationTarget
}