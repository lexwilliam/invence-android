package com.lexwilliam.order.cart.route

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.component.button.InvenceFloatingActionButton
import com.lexwilliam.core_ui.component.drawer.InvenceNavigationDrawer
import com.lexwilliam.core_ui.component.topbar.InvenceTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.order.cart.component.CartCard
import com.lexwilliam.order.cart.navigation.CartNavigationTarget
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartRoute(
    viewModel: CartViewModel = hiltViewModel(),
    toOrder: (UUID) -> Unit,
    onDrawerNavigation: (String) -> Unit
) {
    val orderGroup by viewModel.orderGroup.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.navigation) { target ->
        when (target) {
            is CartNavigationTarget.Order -> toOrder(target.uuid)
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    InvenceNavigationDrawer(
        currentScreen = Screen.CART,
        drawerState = drawerState,
        onNavigationItemClick = onDrawerNavigation
    ) {
        Scaffold(
            containerColor = InvenceTheme.colors.neutral10,
            topBar = {
                InvenceTopBar(
                    title = {
                        Text(
                            text = "Cart",
                            style = InvenceTheme.typography.titleMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "back button"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                InvenceFloatingActionButton(
                    onClick = { viewModel.onEvent(CartUiEvent.AddCartClicked) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "add cart icon")
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier =
                    Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                if (orderGroup.isEmpty()) {
                    item {
                        Box(
                            modifier =
                                Modifier
                                    .padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "You have no order, you can add it by clicking the + button",
                                style = InvenceTheme.typography.bodyMedium,
                                color = InvenceTheme.colors.neutral60,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(items = orderGroup) { group ->
                        CartCard(
                            orderGroup = group,
                            onClick = { viewModel.onEvent(CartUiEvent.CartClicked(group)) },
                            onRemoveClick = {
                                viewModel.onEvent(
                                    CartUiEvent.RemoveCartClicked(group)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}