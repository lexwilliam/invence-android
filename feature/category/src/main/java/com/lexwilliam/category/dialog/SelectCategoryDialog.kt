package com.lexwilliam.category.dialog

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lexwilliam.core_ui.R
import com.lexwilliam.core_ui.component.dialog.SizeAwareDialog
import com.lexwilliam.core_ui.component.image.NetworkImage
import com.lexwilliam.core_ui.component.textfield.InvenceSearchTextField
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.product.model.ProductCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCategoryDialog(
    onDismiss: () -> Unit,
    categories: List<ProductCategory>,
    query: String,
    onQueryChanged: (String) -> Unit,
    formImagePath: Uri? = null,
    formInputImageChanged: (Uri?) -> Unit,
    formTitle: String,
    formTitleChanged: (String) -> Unit,
    formOnConfirm: () -> Unit
) {
    var isFormDialogShown by remember { mutableStateOf(false) }

    SizeAwareDialog(
        onDismiss = onDismiss,
        title = { Text(text = "Select Category", style = InvenceTheme.typography.titleMedium) },
        titleLeading = {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.ArrowBackIos, contentDescription = "nav back")
            }
        },
        isCompact = true
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { isFormDialogShown = true },
                    containerColor = InvenceTheme.colors.primary,
                    contentColor = InvenceTheme.colors.neutral10
                ) {
                    Icon(Icons.Default.Add, contentDescription = "add category fab icon")
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
            ) {
                item {
                    InvenceSearchTextField(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp),
                        value = query,
                        onValueChange = onQueryChanged,
                        placeholder = {
                            Text(
                                text = "Search",
                                style = InvenceTheme.typography.bodyLarge
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.search),
                                contentDescription = "search icon",
                                tint = InvenceTheme.colors.primary
                            )
                        },
                        singleLine = true
                    )
                }
                items(items = categories) { category ->
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        NetworkImage(imagePath = category.imageUrl)
                        Text(text = category.name, style = InvenceTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }

    if (isFormDialogShown) {
        CategoryFormDialog(
            onDismiss = { isFormDialogShown = false },
            isEditing = false,
            imagePath = formImagePath,
            onImageChanged = formInputImageChanged,
            title = formTitle,
            onTitleChanged = formTitleChanged,
            onConfirm = formOnConfirm
        )
    }
}