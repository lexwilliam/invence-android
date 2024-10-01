package com.lexwilliam.order.checkout.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lexwilliam.core.extensions.toCurrency
import com.lexwilliam.core_ui.component.button.InvencePrimaryButton
import com.lexwilliam.core_ui.component.textfield.InvenceOutlineTextField
import com.lexwilliam.core_ui.component.topbar.InvenceTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderAddOnDialog(
    state: OrderAddOnDialogState,
    onEvent: (OrderAddOnDialogEvent) -> Unit,
    subtotal: Double
) {
    Dialog(
        onDismissRequest = { onEvent(OrderAddOnDialogEvent.Dismiss) },
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false
            )
    ) {
        Scaffold(
            containerColor = InvenceTheme.colors.neutral10,
            topBar = {
                InvenceTopBar(
                    title = {
                        Text(
                            text = "Additional Cost",
                            style = InvenceTheme.typography.titleMedium
                        )
                    },
                    actions = {
                        IconButton(onClick = { onEvent(OrderAddOnDialogEvent.Dismiss) }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null
                            )
                        }
                    }
                )
            },
            bottomBar = {
                Box(modifier = Modifier.padding(16.dp)) {
                    InvencePrimaryButton(
                        modifier =
                            Modifier
                                .fillMaxWidth(),
                        onClick = { onEvent(OrderAddOnDialogEvent.Confirm) }
                    ) {
                        Text(text = "Confirm", style = InvenceTheme.typography.labelLarge)
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier =
                    Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                        .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier =
                    Modifier,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "Discount", style = InvenceTheme.typography.titleSmall)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            InvenceOutlineTextField(
                                modifier = Modifier.weight(7f),
                                value = state.discountFixed,
                                onValueChange = {
                                    onEvent(OrderAddOnDialogEvent.DiscountFixedChanged(it))
                                },
                                leadingIcon = {
                                    Text(text = "Rp", style = InvenceTheme.typography.bodyMedium)
                                },
                                placeholder = {
                                    Text(text = "0", style = InvenceTheme.typography.bodyMedium)
                                },
                                keyboardOptions =
                                    KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    ),
                                singleLine = true
                            )
                            Icon(Icons.Default.Add, contentDescription = "add discount")
                            InvenceOutlineTextField(
                                modifier = Modifier.weight(3f),
                                value = state.discountPercent,
                                onValueChange = {
                                    onEvent(OrderAddOnDialogEvent.DiscountPercentChanged(it))
                                },
                                trailingIcon = {
                                    Text(text = "%", style = InvenceTheme.typography.bodyMedium)
                                },
                                placeholder = {
                                    Text(text = "0", style = InvenceTheme.typography.bodyMedium)
                                },
                                keyboardOptions =
                                    KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    ),
                                singleLine = true
                            )
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "Surcharge", style = InvenceTheme.typography.titleSmall)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            InvenceOutlineTextField(
                                modifier = Modifier.weight(7f),
                                value = state.surchargeFixed,
                                onValueChange = {
                                    onEvent(OrderAddOnDialogEvent.SurchargeFixedChanged(it))
                                },
                                leadingIcon = {
                                    Text(text = "Rp", style = InvenceTheme.typography.bodyMedium)
                                },
                                placeholder = {
                                    Text(text = "0", style = InvenceTheme.typography.bodyMedium)
                                },
                                keyboardOptions =
                                    KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    ),
                                singleLine = true
                            )
                            Icon(Icons.Default.Add, contentDescription = "add discount")
                            InvenceOutlineTextField(
                                modifier = Modifier.weight(3f),
                                value = state.surchargePercent,
                                onValueChange = {
                                    onEvent(OrderAddOnDialogEvent.SurchargePercentChanged(it))
                                },
                                trailingIcon = {
                                    Text(text = "%", style = InvenceTheme.typography.bodyMedium)
                                },
                                placeholder = {
                                    Text(text = "0", style = InvenceTheme.typography.bodyMedium)
                                },
                                keyboardOptions =
                                    KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    ),
                                singleLine = true
                            )
                        }
                    }
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val discount = (state.getDiscount()?.calculate(subtotal) ?: 0.0)
                    val surcharge = (state.getSurcharge()?.calculate(subtotal) ?: 0.0)
                    val result = subtotal + discount + surcharge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Subtotal", style = InvenceTheme.typography.titleSmall)
                        Text(
                            text = subtotal.toCurrency(),
                            style = InvenceTheme.typography.titleSmall
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Discount", style = InvenceTheme.typography.titleSmall)
                        Text(
                            text = discount.toCurrency(),
                            style = InvenceTheme.typography.titleSmall
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Surcharge", style = InvenceTheme.typography.titleSmall)
                        Text(
                            text = surcharge.toCurrency(),
                            style = InvenceTheme.typography.titleSmall
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total", style = InvenceTheme.typography.titleMedium)
                        Text(
                            text = result.toCurrency(),
                            style = InvenceTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}