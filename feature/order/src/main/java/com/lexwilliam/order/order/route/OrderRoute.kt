package com.lexwilliam.order.order.route

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.order.order.navigation.OrderNavigationTarget
import java.util.UUID

@Composable
fun OrderRoute(
    viewModel: OrderViewModel = hiltViewModel(),
    toCheckOut: (UUID) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uiProducts by viewModel.uiProducts.collectAsStateWithLifecycle()
    val orderGroup by viewModel.orderGroup.collectAsStateWithLifecycle(initialValue = null)
    val cart by viewModel.cart.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.navigation) { target ->
        when (target) {
            is OrderNavigationTarget.CheckOut -> toCheckOut(target.orderUUID)
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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
                    enabled = !(cart.isEmpty() || uiState.isLoading)
                ) {
                    Text(
                        text = "Check Out",
                        style = InvenceTheme.typography.labelLarge
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier =
                Modifier
                    .padding(innerPadding)
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