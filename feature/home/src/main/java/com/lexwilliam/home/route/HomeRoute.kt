package com.lexwilliam.home.route

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.core.util.getGreetingString
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.component.topbar.InvenceTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.home.component.HomeIconButton
import com.lexwilliam.home.component.ShiftCalendar
import com.lexwilliam.home.model.Inbox
import com.lexwilliam.home.model.homeIcons
import com.lexwilliam.home.navigation.HomeNavigationTarget
import com.lexwilliam.transaction.component.LogCard
import com.lexwilliam.transaction.component.TransactionCard
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    toInventory: () -> Unit,
    toCart: () -> Unit,
    toTransactionDetail: (UUID) -> Unit,
    toTransactionHistory: () -> Unit
) {
    val inbox by viewModel.inbox.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.navigation) { target ->
        when (target) {
            HomeNavigationTarget.Inventory -> toInventory()
            HomeNavigationTarget.Cart -> toCart()
            is HomeNavigationTarget.TransactionDetail -> toTransactionDetail(target.transactionUUID)
            HomeNavigationTarget.TransactionHistory -> toTransactionHistory()
        }
    }

    Scaffold(
        containerColor = InvenceTheme.colors.neutral10,
        topBar = {
            InvenceTopBar(
                title = {
                    Text(
                        text = getGreetingString(),
                        style = InvenceTheme.typography.titleMedium
                    )
                }
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .background(InvenceTheme.colors.neutral10),
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 36.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                ShiftCalendar()
            }
            items(items = homeIcons) { model ->
                HomeIconButton(
                    onClick = { viewModel.onHomeIconClicked(model.label) },
                    icon = model.icon,
                    iconColor = InvenceTheme.colors.primary,
                    backgroundColor = InvenceTheme.colors.secondary,
                    label = model.label
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Inbox",
                        style = InvenceTheme.typography.titleLarge
                    )
                    IconButton(onClick = { viewModel.historyClicked() }) {
                        Icon(Icons.Default.History, contentDescription = "history icon")
                    }
                }
            }
            Log.d("TAG", inbox.toString())
            inbox.forEach { entry ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(text = entry.key, style = InvenceTheme.typography.labelLarge)
                }
                items(
                    items = entry.value,
                    span = { GridItemSpan(maxLineSpan) }
                ) { inbox ->
                    when (inbox) {
                        is Inbox.InboxTransaction -> {
                            TransactionCard(
                                modifier =
                                    Modifier.clickable {
                                        viewModel.transactionClicked(
                                            inbox.transaction
                                        )
                                    },
                                transaction = inbox.transaction
                            )
                        }
                        is Inbox.InboxLog -> {
                            LogCard(
                                log = inbox.log
                            )
                        }
                    }
                }
            }
            if (inbox.values.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    TextButton(onClick = { viewModel.seeAllClicked() }) {
                        Text(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                            text = "See All",
                            textAlign = TextAlign.Center,
                            style = InvenceTheme.typography.titleMedium,
                            color = InvenceTheme.colors.primary
                        )
                    }
                }
            }
        }
    }
}