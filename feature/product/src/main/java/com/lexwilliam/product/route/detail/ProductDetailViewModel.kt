package com.lexwilliam.product.route.detail

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexwilliam.core.extensions.addOrUpdateDuplicate
import com.lexwilliam.product.model.Product
import com.lexwilliam.product.model.ProductItem
import com.lexwilliam.product.navigation.ProductDetailNavigationTarget
import com.lexwilliam.product.route.detail.dialog.RestockDialogEvent
import com.lexwilliam.product.route.detail.dialog.RestockDialogState
import com.lexwilliam.product.usecase.ObserveProductCategoryUseCase
import com.lexwilliam.product.usecase.UpsertProductCategoryUseCase
import com.lexwilliam.user.usecase.FetchUserUseCase
import com.lexwilliam.user.usecase.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class ProductDetailViewModel
    @Inject
    constructor(
        observeProductCategory: ObserveProductCategoryUseCase,
        private val upsertProductCategory: UpsertProductCategoryUseCase,
        observeSession: ObserveSessionUseCase,
        fetchUser: FetchUserUseCase,
        savedStateHandle: SavedStateHandle
    ) : ViewModel() {
        private val _navigation = Channel<ProductDetailNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        private val productUUID =
            savedStateHandle
                .getStateFlow<String?>("productUUID", null)

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

        private val category =
            categories.map { categories ->
                categories.firstOrNull { category ->
                    category.products.any { product ->
                        product.uuid == productUUID.firstOrNull()
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
                            product.uuid == productUUID
                        }
                    product ?: Product()
                } ?: Product()
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                Product()
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
            }
        }

        private fun handleDeleteIconClicked() {
            viewModelScope.launch {
                val category = category.firstOrNull() ?: return@launch
                upsertProductCategory(
                    category =
                        category.copy(
                            products =
                                category.products
                                    .filterNot { product.value.uuid == it.uuid }
                        )
                ).fold(
                    ifLeft = { failure ->
                        Log.d("TAG", failure.toString())
                    },
                    ifRight = {
                        _navigation.send(ProductDetailNavigationTarget.BackStack)
                    }
                )
            }
        }

        private fun handleEditIconClicked() {
            viewModelScope.launch {
                _navigation.send(ProductDetailNavigationTarget.ProductForm(product.value.uuid))
            }
        }

        private fun handleCopyDescription() {
            TODO("Not yet implemented")
        }

        private fun handleBackStackClicked() {
            viewModelScope.launch {
                _navigation.send(ProductDetailNavigationTarget.BackStack)
            }
        }

//        private fun handleQuantityChanged(
//            value: String,
//            instant: Instant
//        ) {
//            _state.update { old ->
//                old.copy(
//                    buyPriceList =
//                        old
//                            .buyPriceList
//                            .toMutableMap()
//                            .apply {
//                                this[instant] =
//                                    UiPriceAndQuantity(
//                                        price = this[instant]?.price ?: "0",
//                                        quantity = value
//                                    )
//                            }
//                )
//            }
//        }

        private fun handleItemExpanded() {
            _state.update { old -> old.copy(itemExpanded = !old.itemExpanded) }
        }

//        private fun handleBuyPriceChanged(
//            value: String,
//            instant: Instant
//        ) {
//            _state.update { old ->
//                old.copy(
//                    buyPriceList =
//                        old
//                            .buyPriceList
//                            .toMutableMap()
//                            .apply {
//                                this[instant] =
//                                    UiPriceAndQuantity(
//                                        price = value,
//                                        quantity = this[instant]?.quantity ?: "0"
//                                    )
//                            }
//                )
//            }
//        }
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
                val modifiedCategory =
                    category.copy(
                        products =
                            category.products
                                .addOrUpdateDuplicate(modifiedProduct) { existingItem, newValue ->
                                    existingItem.uuid == newValue.uuid
                                }
                    )
                upsertProductCategory(modifiedCategory).fold(
                    ifLeft = { failure ->
                        Log.d("TAG", failure.toString())
                    },
                    ifRight = {
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