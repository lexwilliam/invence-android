package com.lexwilliam.transaction.detail.route

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.core.extensions.toCurrency
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.component.image.NetworkImage
import com.lexwilliam.core_ui.component.topbar.InvenceTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.transaction.detail.navigation.TransactionDetailNavigationTarget
import com.lexwilliam.transaction.model.Payment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailRoute(
    viewModel: TransactionDetailViewModel = hiltViewModel(),
    onBackStack: () -> Unit
) {
    val transaction by viewModel.transaction.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.navigation) { target ->
        when (target) {
            TransactionDetailNavigationTarget.BackStackClicked -> onBackStack()
        }
    }

    Scaffold(
        topBar = {
            InvenceTopBar(
                title = {
                    Text(text = "Transaction Detail", style = InvenceTheme.typography.titleMedium)
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleBackStackClicked() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "nav back stack")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (transaction != null) {
            Column(
                modifier =
                    Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "Order", style = InvenceTheme.typography.titleMedium)
                transaction?.orderGroup?.orders?.forEach { order ->
                    TransactionOrderCard(
                        imagePath = order.item.imagePath,
                        name = order.item.name,
                        price = order.item.price,
                        quantity = order.quantity
                    )
                }
                Text(text = "Payment", style = InvenceTheme.typography.titleMedium)
                transaction?.payments?.forEach { payment ->
                    TransactionPaymentCard(payment = payment)
                }
                transaction?.createdBy?.let {
                    Text(text = "Created By: $it", style = InvenceTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun TransactionOrderCard(
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    imagePath: Uri?,
    name: String,
    price: Double,
    quantity: Int
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(6f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier =
                    Modifier
                        .shadow(4.dp, RoundedCornerShape(8.dp), true)
            ) {
                NetworkImage(
                    imagePath = imagePath,
                    modifier =
                        imageModifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(InvenceTheme.colors.neutral10)
                )
            }
            Column(
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = name, style = InvenceTheme.typography.labelLarge)
                    Text(
                        text = price.toCurrency(),
                        style = InvenceTheme.typography.bodyMedium
                    )
                }
            }
        }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(4f),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(text = "$quantity pcs", style = InvenceTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun TransactionPaymentCard(
    modifier: Modifier = Modifier,
    payment: Payment
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = payment.paymentMethod.group, style = InvenceTheme.typography.bodyLarge)
        Text(
            text = payment.total.toCurrency(),
            style = InvenceTheme.typography.bodyLarge
        )
    }
}