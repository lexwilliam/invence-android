package com.lexwilliam.order.checkout.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lexwilliam.core.extensions.toCurrency
import com.lexwilliam.core_ui.component.button.InvencePrimaryButton
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.transaction.model.Transaction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSuccessDialog(
    transaction: Transaction,
    onDone: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDone,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = InvenceTheme.colors.neutral10
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total ${transaction.total.toCurrency()}",
                style = InvenceTheme.typography.titleLarge
            )
            Icon(
                modifier = Modifier.size(56.dp),
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "check circle icon",
                tint = InvenceTheme.colors.primary
            )
            Text(text = "Order Success", style = InvenceTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(32.dp))
            InvencePrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onDone
            ) {
                Text(text = "Done", style = InvenceTheme.typography.labelLarge)
            }
        }
    }
}