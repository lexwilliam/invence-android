package com.lexwilliam.inventory.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexwilliam.inventory.model.UiProduct
import com.lexwilliam.inventory.navigation.InventoryNavigationTarget
import com.lexwilliam.inventory.usecase.ObserveProductInventoryUseCase
import com.lexwilliam.product.usecase.UpsertProductCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel
    @Inject
    constructor(
        observeProduct: ObserveProductInventoryUseCase,
        private val upsertProduct: UpsertProductCategoryUseCase
    ) : ViewModel() {
        private val _state = MutableStateFlow(InventoryUiState())
        val uiState = _state.asStateFlow()

        private val _navigation = Channel<InventoryNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        private val categories = observeProduct()

        @OptIn(ExperimentalCoroutinesApi::class)
        val uiProducts =
            categories.flatMapLatest { categories ->
                flowOf(
                    categories.flatMap { category ->
                        category.products.map { product ->
                            UiProduct(
                                category = category,
                                product = product
                            )
                        }
                    }
                )
            }

        fun onEvent(event: InventoryUiEvent) {
            when (event) {
                is InventoryUiEvent.QueryChanged -> handleQueryChanged(event.value)
                is InventoryUiEvent.FabClicked -> handleFabClicked()
            }
        }

        private fun handleQueryChanged(value: String) {
            _state.update { old ->
                old.copy(
                    queryStrategy = old.queryStrategy.copy(query = value)
                )
            }
        }

        private fun handleFabClicked() {
            viewModelScope.launch {
                _navigation.send(InventoryNavigationTarget.ProductForm(null))
            }
        }
    }