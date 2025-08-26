package com.lexwilliam.category.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lexwilliam.core_ui.R
import com.lexwilliam.core_ui.component.button.InvencePrimaryButton
import com.lexwilliam.core_ui.component.textfield.InvenceSearchTextField
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.product.model.ProductCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCategoryDialog(
    onDismiss: () -> Unit,
    categories: List<ProductCategory>,
    onCategoryClicked: (ProductCategory) -> Unit,
    onFormConfirm: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var isFormShowing by remember { mutableStateOf(false) }

    val filteredCategories =
        categories.filter {
            it.name.contains(query, ignoreCase = true)
        }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = InvenceTheme.colors.neutral10
    ) {
        Column {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Select Category",
                style = InvenceTheme.typography.titleMedium
            )
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.7f)
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
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .clickable { onCategoryClicked(category) }
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
                item {
                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
            }
            InvencePrimaryButton(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                onClick = { isFormShowing = true }
            ) {
                Text("Add Category", style = InvenceTheme.typography.labelLarge)
            }
        }
    }

    if (isFormShowing) {
        CategoryFormDialog(
            onDismiss = { isFormShowing = false },
            category = null,
            onConfirm = { title ->
                onFormConfirm(title)
                isFormShowing = false
            }
        )
    }
}