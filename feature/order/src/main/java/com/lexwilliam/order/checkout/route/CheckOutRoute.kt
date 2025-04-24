package com.lexwilliam.order.checkout.route

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.core.extensions.toCurrency
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.component.button.InvenceOutlineButton
import com.lexwilliam.core_ui.component.button.InvencePrimaryButton
import com.lexwilliam.core_ui.component.topbar.InvenceTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.order.checkout.dialog.OrderAddOnDialog
import com.lexwilliam.order.checkout.dialog.OrderSuccessDialog
import com.lexwilliam.order.checkout.dialog.OrderSuccessDialogEvent
import com.lexwilliam.order.checkout.navigation.CheckOutNavigationTarget
import com.lexwilliam.order.order.component.SmallOrderProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckOutRoute(
    viewModel: CheckOutViewModel = hiltViewModel(),
    onBackStack: () -> Unit,
    toCart: () -> Unit
) {
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()
    val successDialogState by viewModel.successDialogState.collectAsStateWithLifecycle()

    val subtotal = orders.sumOf { order -> order.quantity * (order.item?.price ?: 0.0) }

    ObserveAsEvents(flow = viewModel.navigation) { target ->
        when (target) {
            CheckOutNavigationTarget.BackStack -> onBackStack()
            CheckOutNavigationTarget.Cart -> toCart()
        }
    }

    BackHandler {
        viewModel.onEvent(CheckOutUiEvent.BackStackClicked)
    }

    dialogState?.let { state ->
        OrderAddOnDialog(
            state = state,
            onEvent = viewModel::onDialogEvent,
            subtotal = subtotal
        )
    }

    successDialogState?.let { state ->
        OrderSuccessDialog(
            transaction = state.transaction,
            onDone = {
                viewModel.onSuccessDialogEvent(OrderSuccessDialogEvent.Confirm)
            }
        )
    }

    Scaffold(
        containerColor = InvenceTheme.colors.neutral10,
        topBar = {
            InvenceTopBar(
                title = {
                    Text(
                        text = "Check Out",
                        style = InvenceTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(CheckOutUiEvent.BackStackClicked) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "nav back stack icon")
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (uiState.discount != null || uiState.surcharge != null) {
                    Column(
                        modifier =
                            Modifier
                                .background(InvenceTheme.colors.neutral30)
                                .clickable {
                                    viewModel.onEvent(
                                        CheckOutUiEvent.AddOnClicked(
                                            uiState.discount,
                                            uiState.surcharge
                                        )
                                    )
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier =
                                Modifier
                                    .background(InvenceTheme.colors.neutral30)
                                    .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Subtotal", style = InvenceTheme.typography.titleSmall)
                            Text(
                                text = subtotal.toCurrency(),
                                style = InvenceTheme.typography.titleSmall
                            )
                        }
                        Row(
                            modifier =
                                Modifier
                                    .background(InvenceTheme.colors.neutral30)
                                    .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Discount", style = InvenceTheme.typography.titleSmall)
                            Text(
                                text = (uiState.discount?.calculate(subtotal) ?: 0.0).toCurrency(),
                                style = InvenceTheme.typography.titleSmall
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Surcharge", style = InvenceTheme.typography.titleSmall)
                            Text(
                                text = (uiState.surcharge?.calculate(subtotal) ?: 0.0).toCurrency(),
                                style = InvenceTheme.typography.titleSmall
                            )
                        }
                    }
                } else {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .background(InvenceTheme.colors.neutral30)
                                .clickable { viewModel.onEvent(CheckOutUiEvent.AddOnClicked()) }
                                .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.AddCircle,
                            contentDescription = "add on icon"
                        )
                        Text(
                            text = "Add discount or surcharge",
                            style = InvenceTheme.typography.labelLarge
                        )
                    }
                }
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total",
                        style = InvenceTheme.typography.titleMedium
                    )
                    Text(
                        text = uiState.calculateTotal(subtotal).toCurrency(),
                        style = InvenceTheme.typography.titleMedium
                    )
                }
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InvenceOutlineButton(
                        modifier =
                            Modifier.wrapContentWidth(),
                        onClick = { viewModel.onEvent(CheckOutUiEvent.SaveForLaterClicked) }
                    ) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = "save cart icon"
                        )
                    }
                    InvencePrimaryButton(
                        modifier =
                            Modifier
                                .fillMaxWidth(),
                        onClick = { viewModel.onEvent(CheckOutUiEvent.ConfirmClicked) }
                    ) {
                        Text(
                            text = "Confirm",
                            style = InvenceTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items = orders) { order ->
                if (order.item != null) {
                    SmallOrderProductCard(
                        modifier = Modifier,
                        imagePath = order.item!!.imagePath,
                        imageModifier = Modifier.size(50.dp),
                        name = order.item!!.name,
                        price = order.item!!.price,
                        quantity = order.quantity,
                        onQuantityChanged = {
                            viewModel.onEvent(
                                CheckOutUiEvent.QuantityChanged(order.item!!.uuid, it)
                            )
                        }
                    )
                }
            }
            item {
                Column {
                    Text(
                        text = "Total Order: ${orders.size}",
                        style = InvenceTheme.typography.labelMedium
                    )
                    Text(
                        text = "Total Quantity: ${orders.sumOf { it.quantity }}",
                        style = InvenceTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}