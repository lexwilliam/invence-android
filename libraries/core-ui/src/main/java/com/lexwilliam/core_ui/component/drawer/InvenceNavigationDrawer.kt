package com.lexwilliam.core_ui.component.drawer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lexwilliam.core_ui.navigation.drawerItems
import com.lexwilliam.core_ui.navigation.getDrawerItemIndex
import com.lexwilliam.core_ui.theme.InvenceTheme
import kotlinx.coroutines.launch

@Composable
fun InvenceNavigationDrawer(
    currentScreen: String,
    drawerState: DrawerState,
    onNavigationItemClick: (String) -> Unit,
    drawerContent: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    var selectedItemIndex by remember { mutableIntStateOf(getDrawerItemIndex(currentScreen)) }
    ModalNavigationDrawer(
        scrimColor = InvenceTheme.colors.neutral10,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = InvenceTheme.colors.secondary.copy(alpha = 0.5f),
                drawerTonalElevation = 0.dp
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                drawerItems.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        colors =
                            NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = InvenceTheme.colors.primary,
                                unselectedContainerColor = Color.Transparent,
                                selectedIconColor = InvenceTheme.colors.neutral10,
                                selectedTextColor = InvenceTheme.colors.neutral10
                            ),
                        label = {
                            Text(
                                item.title,
                                style = InvenceTheme.typography.bodyMedium
                            )
                        },
                        icon = {
                            Icon(
                                imageVector =
                                    when (index == selectedItemIndex) {
                                        true -> item.selectedIcon
                                        false -> item.unselectedIcon
                                    },
                                contentDescription = item.title
                            )
                        },
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                            scope.launch {
                                drawerState.close()
                                onNavigationItemClick(item.route)
                            }
                        },
                        modifier =
                            Modifier
                                .padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        },
        drawerState = drawerState
    ) {
        drawerContent()
    }
}