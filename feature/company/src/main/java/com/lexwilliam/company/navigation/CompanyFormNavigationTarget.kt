package com.lexwilliam.company.navigation

sealed interface CompanyFormNavigationTarget {
    data object Inventory : CompanyFormNavigationTarget
}