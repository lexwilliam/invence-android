package com.lexwilliam.product.route.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexwilliam.core_ui.controller.SnackbarController
import com.lexwilliam.core_ui.controller.SnackbarEvent
import com.lexwilliam.core_ui.model.SnackbarTypeEnum
import com.lexwilliam.product.model.ProductItem
import com.lexwilliam.product.navigation.ProductDetailNavigationTarget
import com.lexwilliam.product.route.detail.dialog.RestockDialogEvent
import com.lexwilliam.product.route.detail.dialog.RestockDialogState
import com.lexwilliam.product.usecase.ObserveProductCategoryUseCase
import com.lexwilliam.product.usecase.UpsertProductCategoryUseCase
import com.lexwilliam.product.usecase.UpsertProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductDetailViewModel
    @Inject
    constructor(
        observeProductCategory: ObserveProductCategoryUseCase,
        private val upsertProductCategory: UpsertProductCategoryUseCase,
        private val upsertProduct: UpsertProductUseCase,
        savedStateHandle: SavedStateHandle
    ) : ViewModel() {
        private val _navigation = Channel<ProductDetailNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        private val productUUID =
            savedStateHandle
                .getStateFlow<String?>("productUUID", null)

        private val _categories = observeProductCategory()

        private val category =
            _categories.map { categories ->
                categories.firstOrNull { category ->
                    category.products.any { product ->
                        product.sku == productUUID.firstOrNull()
                    }
                }
            }

        val product =
            combine(
                productUUID,
                category
            ) { productUUID, category ->
                productUUID?.let {
                    val product =
                        category?.products?.firstOrNull { product ->
                            product.sku == productUUID
                        }
                    product
                }
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                null
            )

        private val _state = MutableStateFlow(ProductDetailUiState())
        val uiState = _state.asStateFlow()

        fun onEvent(event: ProductDetailUiEvent) {
            when (event) {
                ProductDetailUiEvent.BackStackClicked -> handleBackStackClicked()
                ProductDetailUiEvent.RestockClicked -> handleRestockClicked()
                ProductDetailUiEvent.ItemExpanded -> handleItemExpanded()
                ProductDetailUiEvent.CopyDescription -> handleCopyDescription()
                ProductDetailUiEvent.EditIconClicked -> handleEditIconClicked()
                ProductDetailUiEvent.DeleteIconClicked -> handleDeleteIconClicked()
                ProductDetailUiEvent.DeleteConfirm -> handleDeleteConfirm()
                ProductDetailUiEvent.DeleteDismiss -> handleDeleteDismiss()
            }
        }

        private fun handleDeleteDismiss() {
            _state.update { old -> old.copy(isDeleteShowing = false) }
        }

        private fun handleDeleteConfirm() {
            viewModelScope.launch {
                val category = category.firstOrNull() ?: return@launch
                upsertProductCategory(
                    category =
                        category.copy(
                            products =
                                category.products
                                    .filterNot { product.value?.sku == it.sku }
                        )
                ).fold(
                    ifLeft = { failure ->
                        SnackbarController.sendEvent(
                            event =
                                SnackbarEvent(
                                    type = SnackbarTypeEnum.ERROR,
                                    message = "Delete Product Failed"
                                )
                        )
                    },
                    ifRight = {
                        _navigation.send(ProductDetailNavigationTarget.BackStack)
                        SnackbarController.sendEvent(
                            event =
                                SnackbarEvent(
                                    type = SnackbarTypeEnum.SUCCESS,
                                    message = "Delete Product Success"
                                )
                        )
                    }
                )
            }
        }

        private fun handleDeleteIconClicked() {
            _state.update { old -> old.copy(isDeleteShowing = true) }
        }

        private fun handleEditIconClicked() {
            viewModelScope.launch {
                val product = product.value ?: return@launch
                _navigation.send(ProductDetailNavigationTarget.ProductForm(product.sku))
            }
        }

        private fun handleCopyDescription() {
        }

        private fun handleBackStackClicked() {
            viewModelScope.launch {
                _navigation.send(ProductDetailNavigationTarget.BackStack)
            }
        }

        private fun handleItemExpanded() {
            _state.update { old -> old.copy(itemExpanded = !old.itemExpanded) }
        }

        private fun handleRestockClicked() {
            _dialogState.update { RestockDialogState() }
        }

        private val _dialogState = MutableStateFlow<RestockDialogState?>(null)
        val dialogState = _dialogState.asStateFlow()

        fun onDialogEvent(event: RestockDialogEvent) {
            when (event) {
                is RestockDialogEvent.BuyPriceChanged -> handleDialogBuyPriceChanged(event.value)
                is RestockDialogEvent.QuantityChanged -> handleDialogQuantityChanged(event.value)
                RestockDialogEvent.Dismiss -> handleDialogDismiss()
                RestockDialogEvent.Confirm -> handleDialogConfirm()
            }
        }

        private fun handleDialogConfirm() {
            viewModelScope.launch {
                val dialogState = _dialogState.value ?: return@launch
                val category = category.firstOrNull() ?: return@launch
                val product = product.firstOrNull() ?: return@launch
                val productItem =
                    ProductItem(
                        itemId = product.items.size + 1,
                        quantity = dialogState.quantity.toInt(),
                        buyPrice = dialogState.buyPrice.toDouble(),
                        createdAt = Clock.System.now()
                    )
                val modifiedProduct = product.copy(items = product.items + productItem)
                upsertProduct(
                    category = category,
                    product = modifiedProduct,
                    image = modifiedProduct.imagePath
                ).fold(
                    ifLeft = { failure ->
                        SnackbarController.sendEvent(
                            event =
                                SnackbarEvent(
                                    type = SnackbarTypeEnum.ERROR,
                                    message = "Upsert Product Category Failed"
                                )
                        )
                    },
                    ifRight = {
                        SnackbarController.sendEvent(
                            event =
                                SnackbarEvent(
                                    type = SnackbarTypeEnum.SUCCESS,
                                    message = "Upsert Product Category Success"
                                )
                        )
                        _dialogState.update { null }
                    }
                )
            }
        }

        private fun handleDialogDismiss() {
            _dialogState.update { null }
        }

        private fun handleDialogQuantityChanged(value: String) {
            _dialogState.update { old -> old?.copy(quantity = value) }
        }

        private fun handleDialogBuyPriceChanged(value: String) {
            _dialogState.update { old -> old?.copy(buyPrice = value) }
        }
    }