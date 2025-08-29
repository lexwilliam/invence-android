package com.lexwilliam.category.route

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.category.dialog.CategoryFormDialog
import com.lexwilliam.core_ui.R
import com.lexwilliam.core_ui.component.textfield.InvenceSearchTextField
import com.lexwilliam.core_ui.component.topbar.InvenceTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.product.model.ProductCategory

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CategoryRoute(
    onBackStack: () -> Unit,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val filteredCategories by viewModel.filteredCategories.collectAsStateWithLifecycle()

    if (state.isEditing) {
        CategoryFormDialog(
            onDismiss = { viewModel.onEvent(CategoryUiEvent.DismissForm) },
            category = state.selectedCategory,
            onConfirm = {
                    name ->
                viewModel.onEvent(CategoryUiEvent.ConfirmForm(state.selectedCategory, name))
            }
        )
    }

    Scaffold(
        containerColor = InvenceTheme.colors.neutral10,
        topBar = {
            InvenceTopBar(
                title = {
                    Text(
                        text = "Category",
                        style = InvenceTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackStack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back icon")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(CategoryUiEvent.OpenForm(null)) },
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
                    .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                InvenceSearchTextField(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    value = state.query,
                    onValueChange = { viewModel.onEvent(CategoryUiEvent.QueryChanged(it)) },
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
                        CategoryCard(
                            category = category,
                            viewModel = viewModel
                        )
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

@Composable
fun CategoryCard(
    category: ProductCategory,
    viewModel: CategoryViewModel
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clickable(
                        indication = ripple(),
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            viewModel.onEvent(
                                CategoryUiEvent.OpenForm(category)
                            )
                        }
                    )
                    .padding(horizontal = 16.dp)
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = category.name,
                style = InvenceTheme.typography.bodyLarge
            )
            IconButton(
                onClick = {
                    viewModel.onEvent(
                        CategoryUiEvent.OpenForm(category)
                    )
                }
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "edit icon"
                )
            }
            IconButton(
                onClick = {
                    viewModel.onEvent(
                        CategoryUiEvent.DeleteCategory(category)
                    )
                }
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "delete icon"
                )
            }
        }
        HorizontalDivider()
    }
}