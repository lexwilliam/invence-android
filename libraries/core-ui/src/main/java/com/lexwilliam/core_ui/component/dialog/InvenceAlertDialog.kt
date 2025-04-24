package com.lexwilliam.core_ui.component.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun InvenceAlertDialog(
    title: String,
    description: String,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit
) {
    AlertDialog(
        containerColor = InvenceTheme.colors.neutral20,
        title = { Text(text = title, style = InvenceTheme.typography.titleLarge) },
        text = { Text(text = description, style = InvenceTheme.typography.bodyMedium) },
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton
    )
}