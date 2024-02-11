package com.lexwilliam.category.dialog

import android.net.Uri
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
import com.lexwilliam.core.model.UploadImageFormat
import com.lexwilliam.core_ui.component.image.InputImage
import com.lexwilliam.core_ui.component.textfield.InvenceOutlineTextField
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun CategoryFormDialog(
    onDismiss: () -> Unit,
    isEditing: Boolean,
    imagePath: UploadImageFormat?,
    onImageChanged: (Uri?) -> Unit,
    title: String,
    onTitleChanged: (String) -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        containerColor = InvenceTheme.colors.neutral20,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditing) "Edit Category" else "Add Category",
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
                    imagePath = imagePath,
                    label = "Upload Category Image",
                    onImageChanged = onImageChanged
                )
                Column {
                    Text(
                        text = "Title",
                        style = InvenceTheme.typography.titleSmall
                    )
                    InvenceOutlineTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = title,
                        onValueChange = onTitleChanged,
                        placeholder = {
                            Text(
                                text = "Input Title",
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