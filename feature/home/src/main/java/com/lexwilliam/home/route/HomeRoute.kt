package com.lexwilliam.home.route

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.core.util.getGreetingString
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.component.chip.InvenceMenuChip
import com.lexwilliam.core_ui.component.image.NetworkImage
import com.lexwilliam.core_ui.component.topbar.InvenceTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.home.component.ProfitGraphCard
import com.lexwilliam.home.model.homeIcons
import com.lexwilliam.home.navigation.HomeNavigationTarget
import com.lexwilliam.transaction.component.TransactionCard
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    toInventory: () -> Unit,
    toCart: () -> Unit,
    toTransactionDetail: (UUID) -> Unit,
    toTransactionHistory: () -> Unit,
    toProfile: () -> Unit
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val shift by viewModel.shift.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.navigation) { target ->
        when (target) {
            HomeNavigationTarget.Inventory -> toInventory()
            HomeNavigationTarget.Cart -> toCart()
            is HomeNavigationTarget.TransactionDetail -> toTransactionDetail(target.transactionUUID)
            HomeNavigationTarget.TransactionHistory -> toTransactionHistory()
            HomeNavigationTarget.Profile -> toProfile()
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
                },
                actions = {
                    if (user?.imageUrl != null) {
                        NetworkImage(
                            modifier =
                                Modifier
                                    .padding(end = 16.dp)
                                    .clip(CircleShape)
                                    .clickable { viewModel.onProfileClicked() }
                        )
                    } else {
                        IconButton(onClick = { viewModel.onProfileClicked() }) {
                            Icon(Icons.Default.Person, contentDescription = "user icon")
                        }
                    }
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
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 36.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                ProfitGraphCard(
                    state = uiState,
                    onPrevious = { viewModel.previousDateClicked() },
                    onNext = { viewModel.nextDateClicked() },
                    onClick = { viewModel.checkInClicked() }
                )
            }
            items(items = homeIcons) { model ->
                InvenceMenuChip(
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
            transactions.forEach { entry ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(text = entry.key, style = InvenceTheme.typography.labelLarge)
                }
                items(
                    items = entry.value,
                    span = { GridItemSpan(maxLineSpan) }
                ) { transaction ->
                    TransactionCard(
                        modifier =
                            Modifier.clickable {
                                viewModel.transactionClicked(
                                    transaction
                                )
                            },
                        transaction = transaction
                    )
                }
            }
            if (transactions.isNotEmpty()) {
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