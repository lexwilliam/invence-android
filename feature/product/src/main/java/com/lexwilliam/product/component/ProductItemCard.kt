package com.lexwilliam.product.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lexwilliam.core.extensions.toCurrency
import com.lexwilliam.core_ui.component.button.InvenceTextButton
import com.lexwilliam.core_ui.component.button.defaults.InvenceButtonDefaults
import com.lexwilliam.core_ui.component.textfield.InvenceOutlineTextField
import com.lexwilliam.core_ui.component.textfield.InvenceQuantityTextField
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun ProductItemCard(
    modifier: Modifier = Modifier,
    itemId: Int,
    buyPrice: String,
    quantity: String,
    readOnly: Boolean = true,
    onBuyPriceChanged: (String) -> Unit = {},
    onQuantityChanged: (String) -> Unit = {},
    onRemoveItem: () -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        Column(
            modifier =
                Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Item $itemId",
                    style = InvenceTheme.typography.bodyMedium
                )
                if (!readOnly) {
                    InvenceTextButton(
                        colors =
                            InvenceButtonDefaults.textButtonColors(
                                contentColor = InvenceTheme.colors.error
                            ),
                        onClick = onRemoveItem
                    ) {
                        Text(
                            text = "Remove",
                            style = InvenceTheme.typography.bodyMedium
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(0.2f),
                        text = "Cost",
                        style = InvenceTheme.typography.titleSmall
                    )
                    when (readOnly) {
                        true -> {
                            Text(
                                text = (buyPrice.toDoubleOrNull() ?: 0.0).toCurrency(),
                                style = InvenceTheme.typography.bodyMedium
                            )
                        }
                        false -> {
                            InvenceOutlineTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = buyPrice,
                                onValueChange = onBuyPriceChanged,
                                placeholder = {
                                    Text(
                                        text = "Input Cost",
                                        style = InvenceTheme.typography.bodyMedium
                                    )
                                },
                                leadingIcon = {
                                    Text(text = "Rp", style = InvenceTheme.typography.bodyMedium)
                                },
                                keyboardOptions =
                                    KeyboardOptions(
                                        keyboardType = KeyboardType.Decimal,
                                        imeAction = ImeAction.Next
                                    ),
                                singleLine = true
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(0.2f),
                        text = "Quantity",
                        style = InvenceTheme.typography.titleSmall
                    )
                    when (readOnly) {
                        true -> {
                            Text(text = "$quantity pcs", style = InvenceTheme.typography.bodyMedium)
                        }
                        false -> {
                            InvenceQuantityTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = quantity,
                                onValueChange = onQuantityChanged,
                                keyboardOptions =
                                    KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    )
                            )
                        }
                    }
                }
            }
        }
        Divider(modifier = Modifier.fillMaxWidth())
    }
}