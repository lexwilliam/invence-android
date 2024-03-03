package com.lexwilliam.order.checkout.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lexwilliam.branch.model.BranchPaymentMethod
import com.lexwilliam.core.extensions.toCurrency
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun PaymentListDialog(
    paymentMethod: List<BranchPaymentMethod>,
    subtotal: Double,
    onMethodClicked: (BranchPaymentMethod) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 300.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(InvenceTheme.colors.neutral20)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Payment", style = InvenceTheme.typography.titleSmall)
                Icon(
                    modifier =
                        Modifier
                            .clickable { },
                    imageVector = Icons.Default.Add,
                    contentDescription = "add payment method icon"
                )
            }
            if (paymentMethod.isEmpty()) {
                Box(
                    modifier = Modifier.height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "There is no payment method in this branch",
                        style = InvenceTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                paymentMethod.groupBy { it.group }.forEach { methods ->
                    Text(text = methods.key, style = InvenceTheme.typography.titleMedium)
                    Divider(Modifier.fillMaxWidth())
                    methods.value.forEach { method ->
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { onMethodClicked(method) },
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
}