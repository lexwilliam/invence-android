package com.lexwilliam.transaction.history.route

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.core_ui.component.drawer.InvenceNavigationDrawer
import com.lexwilliam.core_ui.component.topbar.InvenceTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryRoute(
    viewModel: TransactionHistoryViewModel = hiltViewModel(),
    onDrawerNavigation: (String) -> Unit
) {
    val transactions = viewModel.transactions.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    InvenceNavigationDrawer(
        currentScreen = Screen.TRANSACTION_HISTORY,
        drawerState = drawerState,
        onNavigationItemClick = onDrawerNavigation
    ) {
        Scaffold(
            containerColor = InvenceTheme.colors.neutral10,
            topBar = {
                InvenceTopBar(
                    title = {
                        Text(
                            text = "Transaction History",
                            style = InvenceTheme.typography.titleMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "back button")
                        }
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier =
                    Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // TODO: implement paging
//                transactions.forEach { entry ->
//                    item {
//                        Text(text = entry.key, style = InvenceTheme.typography.labelLarge)
//                    }
//                    items(
//                        items = entry.value,
//                        key = { it.uuid }
//                    ) { transaction ->
//                        TransactionCard(
//                            transaction = transaction
//                        )
//                    }
//                }
            }
        }
    }
}