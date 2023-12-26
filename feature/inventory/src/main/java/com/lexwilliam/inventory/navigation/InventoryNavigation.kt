package com.lexwilliam.inventory.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.inventory.route.InventoryRoute

fun NavGraphBuilder.inventoryNavigation() {
    composable(route = Screen.INVENTORY) {
        InventoryRoute()
    }
}
