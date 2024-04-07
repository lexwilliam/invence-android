package com.lexwilliam.transaction.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.lexwilliam.core.extensions.toCurrency
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.log.model.DataLog
import com.lexwilliam.log.model.LogAdd
import com.lexwilliam.log.model.LogDelete
import com.lexwilliam.log.model.LogRestock
import com.lexwilliam.log.model.LogSell
import com.lexwilliam.transaction.model.getLogIcon

@Composable
fun LogCard(
    modifier: Modifier = Modifier,
    log: DataLog
) {
    val logIcon = getLogIcon(log = log)
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(InvenceTheme.colors.neutral10)
                .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .background(logIcon.containerColor, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(logIcon.icon, contentDescription = "transaction icon", tint = logIcon.contentColor)
        }

        return when {
            log.add != null -> LogAddContent(logAdd = log.add)
            log.update != null -> {}
            log.delete != null -> LogDeleteContent(logDelete = log.delete)
            log.sell != null -> LogSellContent(logSell = log.sell)
            log.restock != null -> LogRestockContent(logRestock = log.restock)
            else -> {}
        }
    }
}

@Composable
fun LogAddContent(logAdd: LogAdd?) {
    if (logAdd != null) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                modifier = Modifier.padding(bottom = 4.dp),
                text = "-${logAdd.totalExpense.toCurrency()}",
                style = InvenceTheme.typography.titleMedium,
                color = InvenceTheme.colors.danger
            )
            Text(
                text = logAdd.product.name,
                style = InvenceTheme.typography.bodySmall
            )
            logAdd.product.items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier.weight(6f),
                        text = "#${item.itemId}",
                        style = InvenceTheme.typography.bodySmall
                    )
                    Row(
                        modifier = Modifier.weight(3f),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${item.quantity}x",
                            style = InvenceTheme.typography.bodySmall
                        )
                        Text(
                            text = item.buyPrice.toCurrency(),
                            style = InvenceTheme.typography.labelMedium,
                            color = InvenceTheme.colors.success
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LogDeleteContent(logDelete: LogDelete?) {
    if (logDelete != null) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                modifier = Modifier.padding(bottom = 4.dp),
                text = "Delete ${logDelete.product.name}",
                style = InvenceTheme.typography.titleMedium,
                color = InvenceTheme.colors.danger
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    style = InvenceTheme.typography.bodySmall
                )
                Text(
                    text =
                        logDelete.product.items
                            .sumOf { it.buyPrice * it.quantity }
                            .toCurrency(),
                    style = InvenceTheme.typography.bodySmall
                )
            }
            logDelete.product.items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier.weight(6f),
                        text = "#${item.itemId}",
                        style = InvenceTheme.typography.bodySmall
                    )
                    Row(
                        modifier = Modifier.weight(3f),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${item.quantity}x",
                            style = InvenceTheme.typography.bodySmall
                        )
                        Text(
                            text = item.buyPrice.toCurrency(),
                            style = InvenceTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LogRestockContent(logRestock: LogRestock?) {
    if (logRestock != null) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                modifier = Modifier.padding(bottom = 4.dp),
                text = "-${logRestock.total.toCurrency()}",
                style = InvenceTheme.typography.titleMedium,
                color = InvenceTheme.colors.danger
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    modifier = Modifier.weight(6f),
                    text = logRestock.name,
                    style = InvenceTheme.typography.bodySmall
                )
                Row(
                    modifier = Modifier.weight(3f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${logRestock.addedStock}x",
                        style = InvenceTheme.typography.bodySmall
                    )
                    Text(
                        text = logRestock.price.toCurrency(),
                        style = InvenceTheme.typography.labelMedium
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Row {
                    Text(
                        text = logRestock.originalStock.toString(),
                        style = InvenceTheme.typography.labelLarge
                    )
                    Text(
                        text = " >> ",
                        style = InvenceTheme.typography.labelLarge
                    )
                    Text(
                        text = "${logRestock.updatedStock} pcs",
                        style = InvenceTheme.typography.labelLarge,
                        color = InvenceTheme.colors.success
                    )
                }
            }
        }
    }
}

@Composable
fun LogSellContent(logSell: LogSell?) {
    if (logSell != null) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                modifier = Modifier.padding(bottom = 4.dp),
                text = "+${logSell.totalProfit.toCurrency()}",
                style = InvenceTheme.typography.titleMedium,
                color = InvenceTheme.colors.success
            )
            logSell.soldProducts.forEach { product ->
                val profit = product.items.sumOf { product.getProfit(it) }
                val quantity = product.quantity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier.weight(6f),
                        text = product.name,
                        style = InvenceTheme.typography.bodySmall
                    )
                    Row(
                        modifier = Modifier.weight(3f),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${quantity}x",
                            style = InvenceTheme.typography.bodySmall
                        )
                        Text(
                            text = profit.toCurrency(),
                            style = InvenceTheme.typography.labelMedium,
                            color = InvenceTheme.colors.success
                        )
                    }
                }
            }
        }
    }
}