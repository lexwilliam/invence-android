package com.lexwilliam.transaction.history.route

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.lexwilliam.core.extensions.toFormatString
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.component.drawer.InvenceNavigationDrawer
import com.lexwilliam.core_ui.component.loading.InvenceCircularProgressIndicator
import com.lexwilliam.core_ui.component.topbar.InvenceTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.transaction.component.TransactionCard
import com.lexwilliam.transaction.history.navigation.TransactionHistoryNavigationTarget
import com.lexwilliam.transaction.model.Transaction
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryRoute(
    viewModel: TransactionHistoryViewModel = hiltViewModel(),
    onDrawerNavigation: (String) -> Unit,
    toTransactionDetail: (UUID) -> Unit
) {
    val transactions = viewModel.transactions.collectAsLazyPagingItems()

    ObserveAsEvents(viewModel.navigation) { target ->
        when (target) {
            is TransactionHistoryNavigationTarget.TransactionDetail ->
                toTransactionDetail(
                    target.uuid
                )
        }
    }

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
            Crossfade(
                modifier = Modifier.padding(innerPadding),
                targetState = transactions.loadState.refresh
            ) { loadState ->
                if (transactions.loadState.refresh is LoadState.Loading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        InvenceCircularProgressIndicator()
                    }
                } else {
                    TransactionHistoryList(
                        transactions = transactions,
                        onTransactionClick = { uuid ->
                            viewModel.onTransactionClick(uuid)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionHistoryList(
    transactions: LazyPagingItems<Transaction>,
    onTransactionClick: (UUID) -> Unit
) {
    LazyColumn(
        modifier =
            Modifier
                .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        var date: String? = null
        items(
            transactions.itemCount,
            key = transactions.itemKey { it.uuid }
        ) { index ->
            val transaction = transactions[index]
            if (transaction != null) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val dateStr =
                        transaction.createdAt
                            .toFormatString("EEE, dd MMM yyyy")
                    if (date == null || date != dateStr) {
                        Text(
                            text = dateStr,
                            style = InvenceTheme.typography.labelLarge
                        )
                        date = dateStr
                    }
                    TransactionCard(
                        onClick = { onTransactionClick(transaction.uuid) },
                        transaction = transaction
                    )
                }
            }
        }

        item {
            if (transactions.loadState.append is LoadState.Loading) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    InvenceCircularProgressIndicator(
                        modifier =
                            Modifier
                                .padding(16.dp)
                    )
                }
            }
        }
    }
}