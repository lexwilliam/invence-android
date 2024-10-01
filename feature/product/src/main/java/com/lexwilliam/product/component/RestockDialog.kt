package com.lexwilliam.product.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lexwilliam.core_ui.component.button.InvencePrimaryButton
import com.lexwilliam.core_ui.component.button.InvenceTextButton
import com.lexwilliam.core_ui.component.textfield.InvenceOutlineTextField
import com.lexwilliam.core_ui.component.textfield.InvenceQuantityTextField
import com.lexwilliam.core_ui.theme.InvenceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestockDialog(
    buyPrice: String,
    onBuyPriceChanged: (String) -> Unit,
    quantity: String,
    onQuantityChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        containerColor = InvenceTheme.colors.neutral10,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Restock",
                style = InvenceTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Buy Price",
                        style = InvenceTheme.typography.titleSmall
                    )
                    InvenceOutlineTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = buyPrice,
                        onValueChange = onBuyPriceChanged,
                        placeholder = {
                            Text(
                                text = "Input Price",
                                style = InvenceTheme.typography.bodyLarge
                            )
                        },
                        singleLine = true,
                        leadingIcon = {
                            Text(text = "Rp", style = InvenceTheme.typography.bodyMedium)
                        },
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            )
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Quantity",
                        style = InvenceTheme.typography.titleSmall
                    )
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
        },
        confirmButton = {
            InvencePrimaryButton(onClick = onConfirm) {
                Text(
                    text = "Restock",
                    style = InvenceTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            InvenceTextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    style = InvenceTheme.typography.labelLarge,
                    color = InvenceTheme.colors.primary
                )
            }
        }
    )
}