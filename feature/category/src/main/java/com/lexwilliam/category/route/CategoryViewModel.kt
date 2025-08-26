package com.lexwilliam.category.route

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.lexwilliam.core_ui.controller.SnackbarController
import com.lexwilliam.core_ui.controller.SnackbarEvent
import com.lexwilliam.core_ui.model.SnackbarTypeEnum
import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.usecase.DeleteProductCategoryUseCase
import com.lexwilliam.product.usecase.ObserveProductCategoryUseCase
import com.lexwilliam.product.usecase.UpsertProductCategoryUseCase
import com.lexwilliam.user.usecase.FetchCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CategoryViewModel
    @Inject
    constructor(
        observeProductCategory: ObserveProductCategoryUseCase,
        private val upsertProductCategory: UpsertProductCategoryUseCase,
        private val deleteProductCategory: DeleteProductCategoryUseCase,
        private val fetchCurrentUser: FetchCurrentUserUseCase
    ) : ViewModel() {
        private val _state = MutableStateFlow(CategoryUiState())
        val state = _state.asStateFlow()

        private val _categories = observeProductCategory()

        val filteredCategories =
            _state.flatMapLatest { state ->
                _categories.map { categories ->
                    categories.filter { category ->
                        category.name.contains(state.query, ignoreCase = true)
                    }
                }
            }
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5_000),
                    emptyList()
                )

        fun onEvent(event: CategoryUiEvent) {
            when (event) {
                is CategoryUiEvent.QueryChanged -> handleQueryChanged(event.value)
                is CategoryUiEvent.OpenForm -> handleOpenForm(event.category)
                is CategoryUiEvent.ConfirmForm ->
                    handleConfirmForm(
                        event.category,
                        event.name
                    )
                CategoryUiEvent.DismissForm -> handleDismissForm()
                is CategoryUiEvent.DeleteCategory -> handleDeleteCategory(event.category)
            }
        }

        private fun handleDeleteCategory(category: ProductCategory) {
            viewModelScope.launch {
                when (val result = deleteProductCategory(category = category)) {
                    is Either.Left -> Log.d("TAG", result.value.toString())
                    is Either.Right ->
                        _state.update {
                                old ->
                            old.copy(isEditing = false, selectedCategory = null)
                        }
                }
            }
        }

        private fun handleDismissForm() {
            _state.update { old -> old.copy(isEditing = false, selectedCategory = null) }
        }

        private fun handleConfirmForm(
            category: ProductCategory?,
            name: String
        ) {
            viewModelScope.launch {
                val isEditing = _state.value.isEditing
                if (isEditing) {
                    val modifiedCategory =
                        category?.copy(
                            name = name
                        ) ?: return@launch
                    when (upsertProductCategory(category = modifiedCategory)) {
                        is Either.Left -> {
                            SnackbarController.sendEvent(
                                event =
                                    SnackbarEvent(
                                        type = SnackbarTypeEnum.ERROR,
                                        message = "Upsert Product Category Failed"
                                    )
                            )
                        }
                        is Either.Right -> {
                            SnackbarController.sendEvent(
                                event =
                                    SnackbarEvent(
                                        type = SnackbarTypeEnum.SUCCESS,
                                        message = "Upsert Product Category Success"
                                    )
                            )
                            _state.update {
                                    old ->
                                old.copy(isEditing = false, selectedCategory = null)
                            }
                        }
                    }
                } else {
                    val user = fetchCurrentUser().getOrNull() ?: return@launch
                    val categoryUUID = UUID.randomUUID()
                    val modifiedCategory =
                        ProductCategory(
                            uuid = categoryUUID,
                            userUUID = user.uuid,
                            name = name,
                            products = emptyList(),
                            createdAt = Clock.System.now(),
                            deletedAt = null
                        )
                    when (val result = upsertProductCategory(category = modifiedCategory)) {
                        is Either.Left -> Log.d("TAG", result.value.toString())
                        is Either.Right ->
                            _state.update {
                                    old ->
                                old.copy(shouldNavigateBack = true)
                            }
                    }
                }
            }
        }

        private fun handleOpenForm(category: ProductCategory?) {
            _state.update {
                    old ->
                old.copy(isEditing = true, selectedCategory = category)
            }
        }

        private fun handleQueryChanged(value: String) {
            _state.update { old -> old.copy(query = value) }
        }
    }