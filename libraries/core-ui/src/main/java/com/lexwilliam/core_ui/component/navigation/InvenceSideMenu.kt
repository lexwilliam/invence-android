package com.lexwilliam.core_ui.component.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lexwilliam.core_ui.navigation.drawerItems
import com.lexwilliam.core_ui.navigation.getDrawerItemIndex
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun InvenceSideMenu(
    currentScreen: String,
    onNavigationItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedItemIndex by remember { mutableIntStateOf(getDrawerItemIndex(currentScreen)) }

    Card(
        modifier =
            modifier
                .width(280.dp)
                .fillMaxHeight(),
        colors =
            CardDefaults.cardColors(
                containerColor = InvenceTheme.colors.secondary.copy(alpha = 0.5f)
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            InvenceNavigationHeader(
                modifier = Modifier.padding(16.dp)
            )
            drawerItems.forEachIndexed { index, item ->
                NavigationDrawerItem(
                    colors =
                        NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = InvenceTheme.colors.primary,
                            unselectedContainerColor = Color.Transparent,
                            selectedIconColor = InvenceTheme.colors.neutral10,
                            selectedTextColor = InvenceTheme.colors.neutral10,
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
                            contentDescription = item.title
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            style = InvenceTheme.typography.bodyMedium
                        )
                    },
                    selected = selectedItemIndex == index,
                    onClick = {
                        selectedItemIndex = index
                        onNavigationItemClick(item.route)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                if (index < drawerItems.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}