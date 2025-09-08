package com.lexwilliam.core_ui.component.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lexwilliam.core_ui.component.drawer.InvenceNavigationDrawer
import com.lexwilliam.core_ui.utils.NavigationType
import com.lexwilliam.core_ui.utils.getNavigationType

@Composable
fun InvenceResponsiveNavigation(
    currentScreen: String,
    drawerState: DrawerState,
    onNavigationItemClick: (String) -> Unit,
    content: @Composable () -> Unit
) {
    val navigationType = getNavigationType()

    when (navigationType) {
        NavigationType.NAVIGATION_DRAWER -> {
            InvenceNavigationDrawer(
                currentScreen = currentScreen,
                drawerState = drawerState,
                onNavigationItemClick = onNavigationItemClick
            ) {
                content()
            }
        }

        NavigationType.NAVIGATION_RAIL -> {
            Row(modifier = Modifier.fillMaxSize()) {
                InvenceNavigationRail(
                    currentScreen = currentScreen,
                    onNavigationItemClick = onNavigationItemClick
                )
                Box(modifier = Modifier.weight(1f)) {
                    content()
                }
            }
        }

        NavigationType.PERMANENT_SIDE_MENU -> {
            Row(modifier = Modifier.fillMaxSize()) {
                InvenceSideMenu(
                    currentScreen = currentScreen,
                    onNavigationItemClick = onNavigationItemClick
                )
                Box(modifier = Modifier.weight(1f)) {
                    content()
                }
            }
        }
    }
}