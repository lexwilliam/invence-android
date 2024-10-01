package com.lexwilliam.category.route

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.category.dialog.CategoryFormDialog
import com.lexwilliam.core_ui.R
import com.lexwilliam.core_ui.component.card.ColumnCardWithImage
import com.lexwilliam.core_ui.component.textfield.InvenceSearchTextField
import com.lexwilliam.core_ui.component.topbar.InvenceTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CategoryRoute(
    onBackStack: () -> Unit,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val filteredCategories by viewModel.filteredCategories.collectAsStateWithLifecycle()
    Log.d("TAG", state.query)

    if (state.isEditing) {
        CategoryFormDialog(
            onDismiss = { viewModel.onEvent(CategoryUiEvent.DismissForm) },
            category = state.selectedCategory,
            onConfirm = {
                    name,
                    image ->
                viewModel.onEvent(CategoryUiEvent.ConfirmForm(state.selectedCategory, name, image))
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "back icon")
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
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                InvenceSearchTextField(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
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
                        Box {
                            var expanded by remember {
                                mutableStateOf(false)
                            }
                            ColumnCardWithImage(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .combinedClickable(
                                            onClick = {
                                                viewModel.onEvent(
                                                    CategoryUiEvent.OpenForm(category)
                                                )
                                            },
                                            onLongClick = {
                                                expanded = true
                                            }
                                        ),
                                imageModifier = Modifier.size(64.dp),
                                imagePath = category.imageUrl
                            ) {
                                Text(
                                    text = category.name,
                                    style = InvenceTheme.typography.bodyLarge
                                )
                            }

                            if (expanded) {
                                DropdownMenu(
                                    modifier = Modifier.background(InvenceTheme.colors.neutral10),
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Edit",
                                                style = InvenceTheme.typography.labelLarge
                                            )
                                        },
                                        onClick = {
                                            viewModel.onEvent(
                                                CategoryUiEvent.OpenForm(category)
                                            )
                                            expanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Delete",
                                                style = InvenceTheme.typography.labelLarge
                                            )
                                        },
                                        onClick = {
                                            viewModel.onEvent(
                                                CategoryUiEvent.DeleteCategory(category)
                                            )
                                            expanded = false
                                        }
                                    )
                                }
                            }
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