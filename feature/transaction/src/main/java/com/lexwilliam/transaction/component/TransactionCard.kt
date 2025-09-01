package com.lexwilliam.transaction.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lexwilliam.core.extensions.toCurrency
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.transaction.model.Transaction

@Composable
fun TransactionCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    transaction: Transaction
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(InvenceTheme.colors.neutral10)
                .clickable(
                    indication = ripple(),
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClick
                )
                .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .background(InvenceTheme.colors.tertiary, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.CurrencyExchange, contentDescription = "transaction icon")
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                modifier = Modifier.padding(bottom = 4.dp),
                text = transaction.total.toCurrency(),
                style = InvenceTheme.typography.titleMedium
            )
            transaction.orderGroup.orders.forEach { order ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.weight(3f),
                        text = order.item?.name ?: "",
                        style = InvenceTheme.typography.bodySmall
                    )
                    Text(
                        modifier =
                            Modifier
                                .weight(1f),
                        text = "${order.quantity} pcs",
                        textAlign = TextAlign.End,
                        style = InvenceTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}