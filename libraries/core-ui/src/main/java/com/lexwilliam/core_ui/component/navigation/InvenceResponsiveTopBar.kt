package com.lexwilliam.core_ui.component.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.lexwilliam.core_ui.component.topbar.InvenceTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.core_ui.utils.NavigationType
import com.lexwilliam.core_ui.utils.getNavigationType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvenceResponsiveTopBar(
    title: String,
    drawerState: DrawerState? = null,
    modifier: Modifier = Modifier
) {
    val navigationType = getNavigationType()
    val scope = rememberCoroutineScope()

    when (navigationType) {
        NavigationType.NAVIGATION_DRAWER -> {
            InvenceTopBar(
                title = {
                    Text(
                        text = title,
                        style = InvenceTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    drawerState?.let { state ->
                        IconButton(onClick = {
                            scope.launch {
                                state.open()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                },
                modifier = modifier
            )
        }

        NavigationType.NAVIGATION_RAIL,
        NavigationType.PERMANENT_SIDE_MENU -> {
            InvenceTopBar(
                title = {
                    Text(
                        text = title,
                        style = InvenceTheme.typography.titleMedium
                    )
                },
                modifier = modifier
            )
        }
    }
}