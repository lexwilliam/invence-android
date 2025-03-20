package com.lexwilliam.category.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lexwilliam.core_ui.R
import com.lexwilliam.core_ui.component.card.ColumnCardWithImage
import com.lexwilliam.core_ui.component.textfield.InvenceSearchTextField
import com.lexwilliam.core_ui.component.topbar.InvenceTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.product.model.ProductCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCategoryDialog(
    onDismiss: () -> Unit,
    categories: List<ProductCategory>,
    onCategoryClicked: (ProductCategory) -> Unit,
    onFormConfirm: (String, Any?) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var isFormShowing by remember { mutableStateOf(false) }

    val filteredCategories =
        categories.filter {
            it.name.contains(query, ignoreCase = true)
        }

    Dialog(
        onDismissRequest = onDismiss,
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false
            )
    ) {
        Scaffold(
            containerColor = InvenceTheme.colors.neutral10,
            topBar = {
                InvenceTopBar(
                    title = {
                        Text(
                            text = "Select Category",
                            style = InvenceTheme.typography.titleMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "dismiss dialog")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { isFormShowing = true },
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
                        .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    InvenceSearchTextField(
                        modifier =
                            Modifier
                                .fillMaxWidth(),
                        value = query,
                        onValueChange = { query = it },
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
                when (filteredCategories.isNotEmpty()) {
                    true -> {
                        items(items = filteredCategories) { category ->
                            ColumnCardWithImage(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable { onCategoryClicked(category) },
                                imageModifier = Modifier.size(64.dp),
                                imagePath = category.imageUrl
                            ) {
                                Text(
                                    text = category.name,
                                    style = InvenceTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                    false -> {
                        item {
                            Text(
                                modifier =
                                    Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                text = "No categories found",
                                style = InvenceTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    if (isFormShowing) {
        CategoryFormDialog(
            onDismiss = { isFormShowing = false },
            category = null,
            onConfirm = { title, image ->
                onFormConfirm(title, image)
                isFormShowing = false
            }
        )
    }
}