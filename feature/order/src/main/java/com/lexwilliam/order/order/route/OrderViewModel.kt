package com.lexwilliam.order.order.route

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.optics.copy
import com.lexwilliam.core.extensions.addOrUpdateDuplicate
import com.lexwilliam.core_ui.controller.SnackbarController
import com.lexwilliam.core_ui.controller.SnackbarEvent
import com.lexwilliam.core_ui.model.SnackbarTypeEnum
import com.lexwilliam.order.model.Order
import com.lexwilliam.order.model.OrderItem
import com.lexwilliam.order.order.model.UiCartItem
import com.lexwilliam.order.order.model.UiProduct
import com.lexwilliam.order.order.navigation.OrderNavigationTarget
import com.lexwilliam.order.usecase.ObserveSingleOrderGroupUseCase
import com.lexwilliam.order.usecase.UpsertOrderGroupUseCase
import com.lexwilliam.product.model.Product
import com.lexwilliam.product.usecase.ObserveProductCategoryUseCase
import com.lexwilliam.product.util.queryProductCategory
import com.lexwilliam.user.usecase.FetchUserUseCase
import com.lexwilliam.user.usecase.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class OrderViewModel
    @Inject
    constructor(
        observeProductCategory: ObserveProductCategoryUseCase,
        private val observeSingleOrderGroup: ObserveSingleOrderGroupUseCase,
        observeSession: ObserveSessionUseCase,
        fetchUser: FetchUserUseCase,
        private val upsertOrderGroup: UpsertOrderGroupUseCase,
        savedStateHandle: SavedStateHandle
    ) : ViewModel() {
        private val orderUUID =
            savedStateHandle
                .getStateFlow<String?>("orderUUID", null)
                .map { uuid -> uuid?.let { UUID.fromString(it) } }

        private val _navigation = Channel<OrderNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        private val _state = MutableStateFlow(OrderUiState())
        val uiState = _state.asStateFlow()

        private val branchUUID =
            observeSession().map { session ->
                session.userUUID
                    ?.let { fetchUser(it) }
                    ?.getOrNull()
                    ?.branchUUID
            }

        val orderGroup =
            orderUUID.flatMapLatest { uuid ->
                when (uuid) {
                    null -> flowOf(null)
                    else ->
                        observeSingleOrderGroup(uuid)
                            .onEach { group ->
                                val cartItems =
                                    group?.orders?.map {
                                            order ->
                                        order.item?.let { item ->
                                            UiCartItem(
                                                product =
                                                    Product(
                                                        sku = item.uuid,
                                                        upc = item.upc,
                                                        name = item.name,
                                                        description = item.description,
                                                        categoryName = item.categoryName,
                                                        sellPrice = item.price,
                                                        items = emptyList(),
                                                        imagePath = item.imagePath,
                                                        createdAt = Clock.System.now()
                                                    ),
                                                quantity = order.quantity
                                            )
                                        }
                                    } ?: emptyList()

                                _cart.value = cartItems.filterNotNull()
                            }
                }
            }

        val uiProducts =
            _state.flatMapLatest { state ->
                branchUUID.flatMapLatest {
                    when (it) {
                        null -> flowOf(emptyList())
                        else ->
                            observeProductCategory(it)
                                .map { categories ->
                                    queryProductCategory(
                                        categories,
                                        state.query
                                    ).flatMap { category ->
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

        private val _cart = MutableStateFlow<List<UiCartItem>>(emptyList())
        val cart = _cart.asStateFlow()

        fun onEvent(event: OrderUiEvent) {
            when (event) {
                is OrderUiEvent.QueryChanged -> handleQueryChanged(event.value)
                OrderUiEvent.BarcodeScannerClicked -> handleBarcodeScannerClicked()
                OrderUiEvent.CheckOutClicked -> handleCheckOutClicked()
                is OrderUiEvent.QuantityChanged ->
                    handleQuantityChanged(
                        event.product,
                        event.quantity
                    )

                OrderUiEvent.BackStackClicked -> handleBackStackClicked()
            }
        }

        private fun handleBackStackClicked() {
            viewModelScope.launch {
                _navigation.send(OrderNavigationTarget.BackStack)
            }
        }

        private fun handleCheckOutClicked() {
            _state.update { old -> old.copy(isLoading = true) }
            viewModelScope.launch {
                val cart = _cart.value
                val orderUUID = orderUUID.firstOrNull() ?: return@launch
                val orderGroup = orderGroup.firstOrNull() ?: return@launch
                val orders =
                    cart.map { item ->
                        Order(
                            uuid = UUID.randomUUID(),
                            item =
                                OrderItem(
                                    uuid = item.product.sku,
                                    upc = item.product.upc,
                                    name = item.product.name,
                                    categoryName = item.product.categoryName,
                                    label = "",
                                    price = item.product.sellPrice,
                                    imagePath = item.product.imagePath,
                                    description = item.product.description
                                ),
                            discounts = emptyList(),
                            quantity = item.quantity,
                            refundedQuantity = 0,
                            note = "",
                            createdAt = Clock.System.now()
                        )
                    }
                upsertOrderGroup(orderGroup.copy(orders = orders)).fold(
                    ifLeft = { failure ->
                        SnackbarController.sendEvent(
                            event =
                                SnackbarEvent(
                                    type = SnackbarTypeEnum.ERROR,
                                    message = "Upsert Order Group Failed"
                                )
                        )
                        _state.update { old -> old.copy(isLoading = false) }
                    },
                    ifRight = {
                        SnackbarController.sendEvent(
                            event =
                                SnackbarEvent(
                                    type = SnackbarTypeEnum.SUCCESS,
                                    message = "Upsert Order Group Success"
                                )
                        )
                        _state.update { old -> old.copy(isLoading = false) }
                        _navigation.send(OrderNavigationTarget.CheckOut(orderUUID))
                    }
                )
            }
        }

        private fun handleQuantityChanged(
            product: Product,
            quantity: Int
        ) {
            _cart.update { old ->
                if (quantity != 0) {
                    old.addOrUpdateDuplicate(
                        UiCartItem(
                            product = product,
                            quantity = quantity
                        )
                    ) { e, n -> e.product.sku == n.product.sku }
                } else {
                    old.filterNot { cart -> cart.product.sku == product.sku }
                }
            }
            Log.d("TAG", _cart.value.toString())
        }

        private fun handleBarcodeScannerClicked() {
            TODO("Not yet implemented")
        }

        private fun handleQueryChanged(value: String) {
            _state.update { old -> old.copy(query = old.query.copy(query = value)) }
        }
    }