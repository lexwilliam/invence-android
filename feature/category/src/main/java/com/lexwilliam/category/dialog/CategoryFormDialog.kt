package com.lexwilliam.category.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lexwilliam.core_ui.component.textfield.InvenceOutlineTextField
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.product.model.ProductCategory

@Composable
fun CategoryFormDialog(
    onDismiss: () -> Unit,
    category: ProductCategory?,
    onConfirm: (String) -> Unit
) {
    var title by remember { mutableStateOf(category?.name ?: "") }
    AlertDialog(
        containerColor = InvenceTheme.colors.neutral10,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (category != null) "Edit Category" else "Add Category",
                style = InvenceTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Title",
                        style = InvenceTheme.typography.titleSmall
                    )
                    InvenceOutlineTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = title,
                        onValueChange = { title = it },
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
            TextButton(onClick = { onConfirm(title) }) {
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