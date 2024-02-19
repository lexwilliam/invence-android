package com.lexwilliam.order.checkout.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lexwilliam.branch.model.BranchPaymentMethod
import com.lexwilliam.core.extensions.toCurrency
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun PaymentListDialog(
    paymentMethod: List<BranchPaymentMethod>,
    subtotal: Double,
    onMethodClicked: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column {
            paymentMethod.groupBy { it.group }.forEach { methods ->
                Text(text = methods.key, style = InvenceTheme.typography.titleMedium)
                methods.value.forEach { method ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = method.name,
                            style = InvenceTheme.typography.titleSmall
                        )
                        Text(
                            text = method.fee?.calculate(subtotal)?.toCurrency() ?: "Invalid",
                            style = InvenceTheme.typography.labelLarge
                        )
                    }
                    if (method != methods.value.last()) {
                        Divider(modifier = Modifier.fillMaxWidth())
                    }
                }

            }
        }
    }
}