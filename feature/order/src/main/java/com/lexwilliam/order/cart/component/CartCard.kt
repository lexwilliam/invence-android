package com.lexwilliam.order.cart.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lexwilliam.core.extensions.toCurrency
import com.lexwilliam.core_ui.component.image.NetworkImage
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.order.model.OrderGroup
import java.util.Locale

@Composable
fun CartCard(
    orderGroup: OrderGroup,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Column(
        modifier =
            Modifier
                .shadow(2.dp, RoundedCornerShape(8.dp), true)
                .background(InvenceTheme.colors.neutral10)
                .clip(RoundedCornerShape(8.dp))
                .height(IntrinsicSize.Max)
                .clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier =
                Modifier
                    .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${orderGroup.uuid.toString()
                        .split("-")[0]
                        .uppercase(Locale.ROOT)}",
                    style = InvenceTheme.typography.titleMedium
                )
                IconButton(onClick = { onRemoveClick() }) {
                    Icon(Icons.Default.Clear, contentDescription = "remove cart icon")
                }
            }
            Divider(modifier = Modifier.fillMaxWidth())
            if (orderGroup.orders.isNotEmpty()) {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    orderGroup.orders.forEach { order ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                NetworkImage(
                                    modifier =
                                        Modifier
                                            .size(64.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                    imagePath = order.item.imagePath
                                )
                                Column {
                                    Text(
                                        text = order.item.name,
                                        style = InvenceTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = order.item.price.toCurrency(),
                                        style = InvenceTheme.typography.labelMedium
                                    )
                                }
                            }
                            Text(
                                text = "x${order.quantity}",
                                style = InvenceTheme.typography.labelLarge
                            )
                        }
                    }
                    Divider(modifier = Modifier.fillMaxWidth())
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total", style = InvenceTheme.typography.labelLarge)
                        Text(
                            text = orderGroup.totalPrice.toCurrency(),
                            style = InvenceTheme.typography.titleMedium
                        )
                    }
                }
            } else {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "You haven't put any product in the cart yet",
                        style = InvenceTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}