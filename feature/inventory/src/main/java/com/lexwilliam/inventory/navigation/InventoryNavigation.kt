package com.lexwilliam.inventory.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.inventory.route.InventoryRoute
import java.util.UUID

fun NavGraphBuilder.inventoryNavigation(toProductForm: (UUID?) -> Unit) {
    composable(route = Screen.INVENTORY) {
        InventoryRoute(
            toProductForm = toProductForm
        )
    }
}

fun NavController.navigateToInventory(options: NavOptions? = null) {
    this.navigate(Screen.INVENTORY, options)
}