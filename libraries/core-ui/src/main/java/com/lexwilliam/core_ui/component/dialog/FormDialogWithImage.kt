package com.lexwilliam.core_ui.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lexwilliam.core_ui.component.image.InputImage
import com.lexwilliam.core_ui.component.textfield.InvenceOutlineTextField
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun FormDialogWithImage(
    onDismiss: () -> Unit,
    image: Any?,
    inputImageLabel: String = "Upload Image",
    onImageChanged: (Any?) -> Unit,
    title: String,
    label: String,
    placeHolder: String,
    name: String,
    onNameChanged: (String) -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        containerColor = InvenceTheme.colors.neutral10,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = InvenceTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InputImage(
                    imageModifier =
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                    image = image,
                    label = inputImageLabel,
                    onImageChanged = onImageChanged
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = label,
                        style = InvenceTheme.typography.titleSmall
                    )
                    InvenceOutlineTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = name,
                        onValueChange = onNameChanged,
                        placeholder = {
                            Text(
                                text = placeHolder,
                                style = InvenceTheme.typography.bodyLarge
                            )
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Confirm",
                    style = InvenceTheme.typography.labelLarge,
                    color = InvenceTheme.colors.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    style = InvenceTheme.typography.labelLarge,
                    color = InvenceTheme.colors.primary
                )
            }
        }
    )
}