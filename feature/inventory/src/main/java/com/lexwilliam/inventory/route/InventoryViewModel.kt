package com.lexwilliam.inventory.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexwilliam.inventory.model.UiProduct
import com.lexwilliam.inventory.navigation.InventoryNavigationTarget
import com.lexwilliam.product.model.Product
import com.lexwilliam.product.usecase.ObserveProductCategoryUseCase
import com.lexwilliam.user.usecase.FetchUserUseCase
import com.lexwilliam.user.usecase.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class InventoryViewModel
    @Inject
    constructor(
        observeProductCategory: ObserveProductCategoryUseCase,
        observeSession: ObserveSessionUseCase,
        fetchUser: FetchUserUseCase
    ) : ViewModel() {
        private val _state = MutableStateFlow(InventoryUiState())
        val uiState = _state.asStateFlow()

        private val _navigation = Channel<InventoryNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        private val branchUUID =
            observeSession().map { session ->
                session.userUUID
                    ?.let { fetchUser(it) }
                    ?.getOrNull()
                    ?.branchUUID
            }

        private val categories =
            branchUUID.flatMapLatest {
                when (it) {
                    null -> flowOf(emptyList())
                    else -> observeProductCategory(it)
                }
            }

        val filteredUiProducts =
            _state.flatMapLatest { state ->
                branchUUID.flatMapLatest {
                    when (it) {
                        null -> flowOf(emptyList())
                        else ->
                            observeProductCategory(it, state.query)
                                .map { categories ->
                                    categories.flatMap { category ->
                                        category.products.map { product ->
                                            UiProduct(
                                                category = category,
                                                product = product
                                            )
                                        }
                                    }
                                }
                    }
                }
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

        val uiProducts =
            categories.map { categories ->
                categories.flatMap { category ->
                    category.products.map { product ->
                        UiProduct(
                            category = category,
                            product = product
                        )
                    }
                }
            }

        fun onEvent(event: InventoryUiEvent) {
            when (event) {
                is InventoryUiEvent.QueryChanged -> handleQueryChanged(event.value)
                is InventoryUiEvent.FabClicked -> handleFabClicked()
                is InventoryUiEvent.ProductClicked -> handleProductClicked(event.product)
                InventoryUiEvent.BarcodeScannerClicked -> handleBarcodeScannerClicked()
            }
        }

        private fun handleBarcodeScannerClicked() {
            viewModelScope.launch {
                _navigation.send(InventoryNavigationTarget.Barcode)
            }
        }

        private fun handleProductClicked(product: Product) {
            viewModelScope.launch {
                _navigation.send(InventoryNavigationTarget.ProductDetail(product.uuid))
            }
        }

        private fun handleQueryChanged(value: String) {
            _state.update { old ->
                old.copy(
                    query = old.query.copy(query = value)
                )
            }
        }

        private fun handleFabClicked() {
            viewModelScope.launch {
                _navigation.send(InventoryNavigationTarget.ProductForm(null))
            }
        }
    }