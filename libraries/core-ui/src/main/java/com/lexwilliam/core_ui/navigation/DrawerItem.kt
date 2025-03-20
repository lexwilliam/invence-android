package com.lexwilliam.core_ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.lexwilliam.core.navigation.Screen

data class DrawerItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
)

val drawerItems =
    listOf(
        DrawerItem(
            title = "Inventory",
            selectedIcon = Icons.Default.Inventory,
            unselectedIcon = Icons.Outlined.Inventory2,
            route = Screen.INVENTORY
        ),
        DrawerItem(
            title = "Order",
            selectedIcon = Icons.Default.ShoppingCart,
            unselectedIcon = Icons.Outlined.ShoppingCart,
            route = Screen.CART
        ),
        DrawerItem(
            title = "Analytics",
            selectedIcon = Icons.Default.BarChart,
            unselectedIcon = Icons.Outlined.BarChart,
            route = Screen.ANALYTICS
        ),
        DrawerItem(
            title = "Transaction History",
            selectedIcon = Icons.Default.History,
            unselectedIcon = Icons.Outlined.History,
            route = Screen.TRANSACTION_HISTORY
        ),
        DrawerItem(
            title = "Profile",
            selectedIcon = Icons.Default.Person,
            unselectedIcon = Icons.Outlined.Person,
            route = Screen.PROFILE
        )
    )

fun getDrawerItemIndex(currentScreen: String): Int {
    return drawerItems.indexOfFirst { it.route == currentScreen }
}