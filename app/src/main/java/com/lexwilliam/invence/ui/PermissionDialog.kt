package com.lexwilliam.invence.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lexwilliam.core.permission.PermissionTextProvider
import com.lexwilliam.core_ui.component.button.InvenceTextButton
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun PermissionDialog(
    permission: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClicked: () -> Unit,
    onGoToAppSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        containerColor = InvenceTheme.colors.neutral10,
        onDismissRequest = onDismiss,
        confirmButton = {
            InvenceTextButton(
                onClick = {
                    when (isPermanentlyDeclined) {
                        true -> onGoToAppSettings()
                        false -> onOkClicked()
                    }
                }
            ) {
                Text("Grant Permission", style = InvenceTheme.typography.labelLarge)
            }
        },
        title = {
            Text("Permission Required", style = InvenceTheme.typography.titleMedium)
        },
        text = {
            Text(
                text = permission.getDescription(isPermanentlyDeclined),
                style = InvenceTheme.typography.bodyMedium
            )
        },
        modifier = modifier
    )
}