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
import com.lexwilliam.order.checkout.dialog.OrderAddOnDialogEvent
import com.lexwilliam.order.checkout.dialog.OrderAddOnDialogState
import com.lexwilliam.order.checkout.dialog.OrderSuccessDialogEvent
import com.lexwilliam.order.checkout.dialog.OrderSuccessDialogState
import com.lexwilliam.order.checkout.route.CheckOutUiEvent
import com.lexwilliam.order.checkout.route.CheckOutUiState
import com.lexwilliam.order.model.Order
import com.lexwilliam.order.model.OrderDiscount
import com.lexwilliam.order.model.OrderItem
import com.lexwilliam.order.model.OrderTax
import com.lexwilliam.order.order.model.UiCartItem
import com.lexwilliam.order.order.model.UiProduct
import com.lexwilliam.order.order.navigation.OrderNavigationTarget
import com.lexwilliam.order.usecase.DeleteOrderGroupUseCase
import com.lexwilliam.order.usecase.ObserveSingleOrderGroupUseCase
import com.lexwilliam.order.usecase.UpsertOrderGroupUseCase
import com.lexwilliam.product.model.Product
import com.lexwilliam.product.usecase.ObserveProductCategoryUseCase
import com.lexwilliam.product.util.queryProductCategory
import com.lexwilliam.transaction.model.Transaction
import com.lexwilliam.transaction.usecase.UpsertTransactionUseCase
import com.lexwilliam.user.usecase.FetchCurrentUserUseCase
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
        private val upsertOrderGroup: UpsertOrderGroupUseCase,
        private val deleteOrderGroup: DeleteOrderGroupUseCase,
        private val upsertTransaction: UpsertTransactionUseCase,
        private val fetchCurrentUserUseCase: FetchCurrentUserUseCase,
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

        // Checkout related state
        private val _checkoutState = MutableStateFlow(CheckOutUiState())
        val checkoutState = _checkoutState.asStateFlow()

        private val _dialogState = MutableStateFlow<OrderAddOnDialogState?>(null)
        val dialogState = _dialogState.asStateFlow()

        private val _successDialogState = MutableStateFlow<OrderSuccessDialogState?>(null)
        val successDialogState = _successDialogState.asStateFlow()

        val _categories = observeProductCategory()

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
            _state.combine(_categories) { state, categories ->
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
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

        private val _cart = MutableStateFlow<List<UiCartItem>>(emptyList())
        val cart = _cart.asStateFlow()

        private val _orders = MutableStateFlow<List<Order>>(emptyList())
        val orders = _orders.asStateFlow()

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
                        _state.update { old -> old.copy(isLoading = false) }
                        _orders.value = orders
                        // Show checkout dialog instead of navigating
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
            viewModelScope.launch {
                SnackbarController.sendEvent(
                    event =
                        SnackbarEvent(
                            type = SnackbarTypeEnum.SUCCESS,
                            message = "Order Scan Feature is coming soon"
                        )
                )
            }
        }

        private fun handleQueryChanged(value: String) {
            _state.update { old -> old.copy(query = old.query.copy(query = value)) }
        }

        // Checkout event handlers
        fun onCheckoutEvent(event: CheckOutUiEvent) {
            when (event) {
                is CheckOutUiEvent.AddOnClicked ->
                    handleAddOnClicked(
                        event.discount,
                        event.surcharge
                    )
                is CheckOutUiEvent.QuantityChanged ->
                    handleCheckoutQuantityChanged(
                        event.productUUID,
                        event.quantity
                    )
                CheckOutUiEvent.SaveForLaterClicked -> handleSaveForLaterClicked()
                CheckOutUiEvent.ConfirmClicked -> handleConfirmClicked()
                CheckOutUiEvent.Dismiss -> handleCheckoutDismiss()
                CheckOutUiEvent.BackStackClicked -> handleBackStackClicked()
            }
        }

        fun onDialogEvent(event: OrderAddOnDialogEvent) {
            when (event) {
                OrderAddOnDialogEvent.Dismiss -> _dialogState.value = null
                OrderAddOnDialogEvent.Confirm -> handleAddOnConfirmed()
                is OrderAddOnDialogEvent.DiscountFixedChanged ->
                    handleDiscountFixedChanged(
                        event.value
                    )
                is OrderAddOnDialogEvent.DiscountPercentChanged ->
                    handleDiscountPercentChanged(
                        event.value
                    )
                is OrderAddOnDialogEvent.SurchargeFixedChanged ->
                    handleSurchargeFixedChanged(
                        event.value
                    )
                is OrderAddOnDialogEvent.SurchargePercentChanged ->
                    handleSurchargePercentChanged(
                        event.value
                    )
            }
        }

        fun onSuccessDialogEvent(event: OrderSuccessDialogEvent) {
            when (event) {
                OrderSuccessDialogEvent.Confirm -> handleSuccessDialogConfirmed()
            }
        }

        private fun handleAddOnClicked(
            discount: OrderDiscount?,
            surcharge: OrderTax?
        ) {
            // Show add-on dialog
            _dialogState.value = OrderAddOnDialogState.from(discount, surcharge)
        }

        private fun handleCheckoutQuantityChanged(
            itemUUID: String,
            quantity: Int
        ) {
            _orders.update { orders ->
                orders.map { order ->
                    if (order.item?.uuid == itemUUID) {
                        order.copy(quantity = quantity)
                    } else {
                        order
                    }
                }
            }
        }

        private fun handleSaveForLaterClicked() {
            // Save order to Firestore without creating transaction
            viewModelScope.launch {
                val orderGroup = orderGroup.firstOrNull() ?: return@launch
                val orders = _orders.value

                upsertOrderGroup(orderGroup.copy(orders = orders)).fold(
                    ifLeft = { failure ->
                        SnackbarController.sendEvent(
                            event =
                                SnackbarEvent(
                                    type = SnackbarTypeEnum.ERROR,
                                    message = "Failed to save order"
                                )
                        )
                    },
                    ifRight = {
                        SnackbarController.sendEvent(
                            event =
                                SnackbarEvent(
                                    type = SnackbarTypeEnum.SUCCESS,
                                    message = "Order saved successfully"
                                )
                        )
                        // Close checkout dialog
                        _orders.value = emptyList()
                    }
                )
            }
        }

        private fun handleConfirmClicked() {
            // Process transaction without creating order doc
            viewModelScope.launch {
                _checkoutState.update { it.copy(isLoading = true) }

                try {
                    val orders = _orders.value
                    val currentUser = fetchCurrentUserUseCase().getOrNull() ?: return@launch
                    val orderGroup = orderGroup.firstOrNull() ?: return@launch

                    // Create transaction directly
                    val transaction =
                        Transaction(
                            uuid = UUID.randomUUID(),
                            userUUID = currentUser.uuid,
                            orderGroup = orderGroup,
                            customer = "",
                            total = orders.sumOf { it.quantity * (it.item?.price ?: 0.0) },
                            profit = orders.sumOf { it.quantity * (it.item?.price ?: 0.0) },
                            createdBy = currentUser.name,
                            createdAt = Clock.System.now()
                        )

                    upsertTransaction(transaction).fold(
                        ifLeft = { failure ->
                            SnackbarController.sendEvent(
                                event =
                                    SnackbarEvent(
                                        type = SnackbarTypeEnum.ERROR,
                                        message = "Transaction failed"
                                    )
                            )
                        },
                        ifRight = {
                            // Show success dialog
                            _successDialogState.value =
                                OrderSuccessDialogState(transaction = transaction)
                        }
                    )
                } finally {
                    _checkoutState.update { it.copy(isLoading = false) }
                }
            }
        }

        private fun handleAddOnConfirmed() {
            // Apply discount/surcharge from dialog
            val dialogState = _dialogState.value ?: return
            val discount = dialogState.getDiscount()
            val surcharge = dialogState.getSurcharge()

            _checkoutState.update {
                it.copy(
                    discount = discount,
                    surcharge = surcharge
                )
            }
            _dialogState.value = null
        }

        private fun handleDiscountFixedChanged(value: String) {
            _dialogState.update { it?.copy(discountFixed = value) }
        }

        private fun handleDiscountPercentChanged(value: String) {
            _dialogState.update { it?.copy(discountPercent = value) }
        }

        private fun handleSurchargeFixedChanged(value: String) {
            _dialogState.update { it?.copy(surchargeFixed = value) }
        }

        private fun handleSurchargePercentChanged(value: String) {
            _dialogState.update { it?.copy(surchargePercent = value) }
        }

        private fun handleSuccessDialogConfirmed() {
            // Close success dialog and reset checkout state
            _successDialogState.value = null
            _orders.value = emptyList()
            // Optionally navigate back or show confirmation
        }

        private fun handleCheckoutDismiss() {
            // Clear orders to hide the checkout dialog
            _orders.value = emptyList()
        }
    }