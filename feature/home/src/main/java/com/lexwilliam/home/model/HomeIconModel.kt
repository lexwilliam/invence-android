package com.lexwilliam.home.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.ui.graphics.vector.ImageVector

data class HomeIconModel(
    val icon: ImageVector,
    val label: String
)

internal val homeIcons =
    listOf(
        HomeIconModel(icon = Icons.Default.Inventory, label = "Inventory"),
        HomeIconModel(icon = Icons.Default.ShoppingCartCheckout, label = "Order")
    )