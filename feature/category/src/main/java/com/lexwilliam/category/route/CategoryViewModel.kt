package com.lexwilliam.category.route

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import arrow.optics.copy
import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.usecase.DeleteProductCategoryUseCase
import com.lexwilliam.product.usecase.ObserveProductCategoryUseCase
import com.lexwilliam.product.usecase.UploadCategoryImageUseCase
import com.lexwilliam.product.usecase.UpsertProductCategoryUseCase
import com.lexwilliam.user.usecase.FetchUserUseCase
import com.lexwilliam.user.usecase.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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
        private val uploadCategoryImageUseCase: UploadCategoryImageUseCase,
        observeSession: ObserveSessionUseCase,
        private val fetchUser: FetchUserUseCase
    ) : ViewModel() {
        private val _state = MutableStateFlow(CategoryUiState())
        val state = _state.asStateFlow()

        private val branchUUID =
            observeSession().map { session ->
                session.userUUID
                    ?.let { fetchUser(it) }
                    ?.getOrNull()
                    ?.branchUUID
            }

        private val _categories =
            branchUUID.flatMapLatest {
                when (it) {
                    null -> flowOf(emptyList())
                    else -> observeProductCategory(it)
                }
            }

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
                        event.name,
                        event.image
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
            name: String,
            image: Bitmap?
        ) {
            viewModelScope.launch {
                val isEditing = _state.value.isEditing
                val branchUUID = branchUUID.firstOrNull() ?: return@launch
                if (isEditing) {
                    val modifiedCategory =
                        category?.copy(
                            name = name
                        ) ?: return@launch
                    when (val result = upsertProductCategory(category = modifiedCategory)) {
                        is Either.Left -> Log.d("TAG", result.value.toString())
                        is Either.Right ->
                            _state.update {
                                    old ->
                                old.copy(isEditing = false, selectedCategory = null)
                            }
                    }
                    uploadCategoryImageUseCase(
                        branchUUID = branchUUID,
                        categoryUUID = modifiedCategory.uuid,
                        bmp = image ?: return@launch
                    ).fold(
                        ifLeft = {
                            Log.d("TAG", it.toString())
                        },
                        ifRight = {
                            when (val result = upsertProductCategory(category = modifiedCategory)) {
                                is Either.Left -> Log.d("TAG", result.value.toString())
                                is Either.Right ->
                                    _state.update {
                                            old ->
                                        old.copy(isEditing = false, selectedCategory = null)
                                    }
                            }
                        }
                    )
                } else {
                    val categoryUUID = UUID.randomUUID()
                    uploadCategoryImageUseCase(
                        branchUUID = branchUUID,
                        categoryUUID = categoryUUID,
                        bmp = image ?: return@launch
                    ).fold(
                        ifLeft = {
                            Log.d("TAG", it.toString())
                        },
                        ifRight = {
                            val modifiedCategory =
                                ProductCategory(
                                    uuid = categoryUUID,
                                    branchUUID = branchUUID,
                                    imageUrl = it,
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
                    )
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