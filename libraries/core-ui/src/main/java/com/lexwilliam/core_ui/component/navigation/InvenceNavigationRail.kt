package com.lexwilliam.core_ui.component.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lexwilliam.core_ui.navigation.drawerItems
import com.lexwilliam.core_ui.navigation.getDrawerItemIndex
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun InvenceNavigationRail(
    currentScreen: String,
    onNavigationItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedItemIndex by remember { mutableIntStateOf(getDrawerItemIndex(currentScreen)) }

    NavigationRail(
        containerColor = InvenceTheme.colors.secondary.copy(alpha = 0.5f),
        modifier = modifier.fillMaxHeight()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            InvenceNavigationHeader(
                modifier = Modifier.padding(16.dp),
                isCompact = true
            )
            drawerItems.forEachIndexed { index, item ->
                NavigationRailItem(
                    colors =
                        NavigationRailItemDefaults.colors(
                            indicatorColor = InvenceTheme.colors.primary,
                            selectedIconColor = InvenceTheme.colors.neutral10,
                            selectedTextColor = InvenceTheme.colors.primary,
                            unselectedIconColor = InvenceTheme.colors.primary,
                            unselectedTextColor = InvenceTheme.colors.primary
                        ),
                    icon = {
                        Icon(
                            imageVector =
                                if (index == selectedItemIndex) {
                                    item.selectedIcon
                                } else {
                                    item.unselectedIcon
                                },
                            contentDescription = item.title,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            style = InvenceTheme.typography.labelSmall
                        )
                    },
                    selected = selectedItemIndex == index,
                    onClick = {
                        selectedItemIndex = index
                        onNavigationItemClick(item.route)
                    }
                )

                if (index < drawerItems.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}