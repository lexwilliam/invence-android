package com.lexwilliam.order.order.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.core.extensions.toCurrency
import com.lexwilliam.core_ui.R
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.component.button.InvencePrimaryButton
import com.lexwilliam.core_ui.component.textfield.InvenceSearchTextField
import com.lexwilliam.core_ui.component.topbar.InvenceTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.order.checkout.dialog.CheckoutDialog
import com.lexwilliam.order.checkout.route.CheckOutUiEvent
import com.lexwilliam.order.order.navigation.OrderNavigationTarget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderRoute(
    viewModel: OrderViewModel = hiltViewModel(),
    onBackStack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uiProducts by viewModel.uiProducts.collectAsStateWithLifecycle()
    val orderGroup by viewModel.orderGroup.collectAsStateWithLifecycle(initialValue = null)
    val cart by viewModel.cart.collectAsStateWithLifecycle()

    // Checkout related state
    val checkoutState by viewModel.checkoutState.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()
    val successDialogState by viewModel.successDialogState.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.navigation) { target ->
        when (target) {
            is OrderNavigationTarget.BackStack -> onBackStack()
        }
    }

    Scaffold(
        containerColor = InvenceTheme.colors.neutral10,
        topBar = {
            InvenceTopBar(
                title = { Text(text = "Order", style = InvenceTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(OrderUiEvent.BackStackClicked) }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val total = cart.sumOf { item -> item.quantity * item.product.sellPrice }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total",
                        style = InvenceTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = total.toCurrency(), style = InvenceTheme.typography.titleLarge)
                }
                InvencePrimaryButton(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    onClick = { viewModel.onEvent(OrderUiEvent.CheckOutClicked) },
                    enabled = !cart.isEmpty(),
                    isLoading = uiState.isLoading
                ) {
                    Text(
                        text = "Check Out",
                        style = InvenceTheme.typography.labelLarge
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InvenceSearchTextField(
                    modifier =
                        Modifier
                            .weight(1f),
                    value = uiState.query.query,
                    onValueChange = { viewModel.onEvent(OrderUiEvent.QueryChanged(it)) },
                    placeholder = {
                        Text(
                            text = "Search",
                            style = InvenceTheme.typography.bodyLarge
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = "search icon",
                            tint = InvenceTheme.colors.primary
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { viewModel.onEvent(OrderUiEvent.BarcodeScannerClicked) }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.barcode_scanner),
                                contentDescription = "barcode scan icon",
                                tint = InvenceTheme.colors.primary
                            )
                        }
                    },
                    singleLine = true
                )
            }
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                orderProductList(
                    uiProducts = uiProducts,
                    cart = cart,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }

    // Show checkout dialog when orders are available
    if (orders.isNotEmpty()) {
        val bottomPadding = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
        CheckoutDialog(
            modifier =
                Modifier
                    .background(InvenceTheme.colors.neutral10)
                    .padding(bottom = bottomPadding + 16.dp),
            orders = orders,
            uiState = checkoutState,
            dialogState = dialogState,
            successDialogState = successDialogState,
            onEvent = viewModel::onCheckoutEvent,
            onDialogEvent = viewModel::onDialogEvent,
            onSuccessDialogEvent = viewModel::onSuccessDialogEvent,
            onDismiss = {
                // Clear orders to hide the dialog
                viewModel.onCheckoutEvent(CheckOutUiEvent.Dismiss)
            }
        )
    }
}