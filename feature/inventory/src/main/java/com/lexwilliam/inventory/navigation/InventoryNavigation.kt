package com.lexwilliam.inventory.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.inventory.route.InventoryRoute

fun NavGraphBuilder.inventoryNavigation(
    onBackStack: () -> Unit,
    toProductForm: (String?) -> Unit,
    toProductDetail: (String) -> Unit,
    toCategory: () -> Unit
) {
    composable(route = Screen.INVENTORY) {
        InventoryRoute(
            onBackStack = onBackStack,
            toProductForm = toProductForm,
            toProductDetail = toProductDetail,
            toCategory = toCategory
        )
    }
}

fun NavController.navigateToInventory(options: NavOptions? = null) {
    this.navigate(Screen.INVENTORY, options)
}