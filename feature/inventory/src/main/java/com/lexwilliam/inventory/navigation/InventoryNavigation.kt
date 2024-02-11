package com.lexwilliam.inventory.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.inventory.route.InventoryRoute

fun NavGraphBuilder.inventoryNavigation(
    toProductForm: (String?) -> Unit,
    toProductDetail: (String) -> Unit,
    toBarcode: () -> Unit
) {
    composable(route = Screen.INVENTORY) {
        InventoryRoute(
            toProductForm = toProductForm,
            toProductDetail = toProductDetail,
            toBarcode = toBarcode
        )
    }
}

fun NavController.navigateToInventory(options: NavOptions? = null) {
    this.navigate(Screen.INVENTORY, options)
}