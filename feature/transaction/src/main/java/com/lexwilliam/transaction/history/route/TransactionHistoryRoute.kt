package com.lexwilliam.transaction.history.route

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.component.topbar.InvenceTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.transaction.component.TransactionCard
import com.lexwilliam.transaction.history.navigation.TransactionHistoryNavigationTarget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryRoute(
    viewModel: TransactionHistoryViewModel = hiltViewModel(),
    onBackStack: () -> Unit
) {
    val transactions by viewModel.groupedTransaction.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.navigation) { target ->
        when (target) {
            TransactionHistoryNavigationTarget.BackStack -> onBackStack()
        }
    }

    Scaffold(
        topBar = {
            InvenceTopBar(
                title = {
                    Text(text = "Transaction History", style = InvenceTheme.typography.titleMedium)
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleBackStackClicked() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "nav back stack")
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
            transactions.forEach { entry ->
                item {
                    Text(text = entry.key, style = InvenceTheme.typography.labelLarge)
                }
                items(
                    items = entry.value,
                    key = { it.uuid }
                ) { transaction ->
                    TransactionCard(
                        transaction = transaction
                    )
                }
            }
        }
    }
}