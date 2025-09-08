package com.lexwilliam.order.checkout.route

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.right
import com.lexwilliam.core_ui.controller.SnackbarController
import com.lexwilliam.core_ui.controller.SnackbarEvent
import com.lexwilliam.core_ui.model.SnackbarTypeEnum
import com.lexwilliam.order.checkout.dialog.OrderAddOnDialogEvent
import com.lexwilliam.order.checkout.dialog.OrderAddOnDialogState
import com.lexwilliam.order.checkout.dialog.OrderSuccessDialogEvent
import com.lexwilliam.order.checkout.dialog.OrderSuccessDialogState
import com.lexwilliam.order.checkout.navigation.CheckOutNavigationTarget
import com.lexwilliam.order.model.Order
import com.lexwilliam.order.model.OrderDiscount
import com.lexwilliam.order.model.OrderTax
import com.lexwilliam.order.usecase.DeleteOrderGroupUseCase
import com.lexwilliam.order.usecase.ObserveSingleOrderGroupUseCase
import com.lexwilliam.order.usecase.UpsertOrderGroupUseCase
import com.lexwilliam.order.util.UpsertGroupFailure
import com.lexwilliam.product.model.Product
import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.model.ProductItem
import com.lexwilliam.product.usecase.ObserveProductCategoryUseCase
import com.lexwilliam.product.usecase.UpsertProductCategoryUseCase
import com.lexwilliam.transaction.model.Transaction
import com.lexwilliam.transaction.usecase.UpsertTransactionUseCase
import com.lexwilliam.user.usecase.FetchCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class CheckOutViewModel
    @Inject
    constructor(
        private val observeSingleOrderGroup: ObserveSingleOrderGroupUseCase,
        private val upsertOrderGroup: UpsertOrderGroupUseCase,
        private val deleteOrderGroup: DeleteOrderGroupUseCase,
        private val upsertTransaction: UpsertTransactionUseCase,
        observeProductCategory: ObserveProductCategoryUseCase,
        private val upsertProductCategory: UpsertProductCategoryUseCase,
        private val fetchCurrentUserUseCase: FetchCurrentUserUseCase,
        savedStateHandle: SavedStateHandle
    ) : ViewModel() {
        private val orderUUID =
            savedStateHandle
                .getStateFlow<String?>("orderUUID", null)
                .map { uuid -> uuid?.let { UUID.fromString(it) } }

        private val _navigation = Channel<CheckOutNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        private val categories = observeProductCategory()

        private val _state = MutableStateFlow(CheckOutUiState())
        val state = _state.asStateFlow()

        private val _dialogState = MutableStateFlow<OrderAddOnDialogState?>(null)
        val dialogState = _dialogState.asStateFlow()

        val orderGroup =
            orderUUID.flatMapLatest { uuid ->
                when (uuid) {
                    null -> flowOf(null)
                    else -> observeSingleOrderGroup(uuid)
                }
            }

        private val _orders = MutableStateFlow<List<Order>>(emptyList())
        val orders = _orders.asStateFlow()

        private fun initializeOrder() {
            viewModelScope.launch {
                val orderUUID = orderUUID.firstOrNull() ?: return@launch
                _orders.value = observeSingleOrderGroup(orderUUID)
                    .firstOrNull()?.orders ?: emptyList()
            }
        }

        private val _successDialogState = MutableStateFlow<OrderSuccessDialogState?>(null)
        val successDialogState = _successDialogState.asStateFlow()

        init {
            initializeOrder()
        }

        fun onEvent(event: CheckOutUiEvent) {
            when (event) {
                CheckOutUiEvent.BackStackClicked -> handleBackStackClicked()
                is CheckOutUiEvent.QuantityChanged ->
                    handleQuantityChanged(
                        event.productUUID,
                        event.quantity
                    )
                CheckOutUiEvent.SaveForLaterClicked -> handleSaveForLaterClicked()
                CheckOutUiEvent.ConfirmClicked -> handleConfirmClicked()
                is CheckOutUiEvent.AddOnClicked ->
                    handleAddOnClicked(
                        event.discount,
                        event.surcharge
                    )
            }
        }

        private fun handleAddOnClicked(
            discount: OrderDiscount?,
            surcharge: OrderTax?
        ) {
            _dialogState.update { OrderAddOnDialogState.from(discount, surcharge) }
        }

        private fun handleConfirmClicked() {
            _state.update { old -> old.copy(isLoading = true) }
            viewModelScope.launch {
                val orderGroup = orderGroup.firstOrNull() ?: return@launch
                val categories = categories.firstOrNull() ?: return@launch
                val user = fetchCurrentUserUseCase().getOrNull() ?: return@launch
                val orders = _orders.value
                val transactionUUID = UUID.randomUUID()
                val soldItemsWithProfit =
                    decreaseProductItemQuantity(
                        orders = orders,
                        categories = categories
                    ).getOrElse { message ->
                        SnackbarController.sendEvent(
                            event =
                                SnackbarEvent(
                                    type = SnackbarTypeEnum.ERROR,
                                    message = message
                                )
                        )
                        _state.update { old -> old.copy(isLoading = false) }
                        return@launch
                    }
                val transaction =
                    Transaction(
                        uuid = transactionUUID,
                        userUUID = user.uuid,
                        orderGroup = orderGroup.copy(orders = orders),
                        customer = "",
                        total = orderGroup.totalPrice,
                        profit = soldItemsWithProfit.second,
                        createdBy = user.name,
                        createdAt = Clock.System.now()
                    )

                when (upsertTransaction(transaction)) {
                    is Either.Left -> {
                        _state.update { old -> old.copy(isLoading = false) }
                        SnackbarController.sendEvent(
                            event =
                                SnackbarEvent(
                                    type = SnackbarTypeEnum.ERROR,
                                    message = "Insert transaction failed"
                                )
                        )
                    }
                    is Either.Right -> {
                        when (deleteOrderGroup(orderGroup)) {
                            is Either.Left -> {
                                _state.update { old -> old.copy(isLoading = false) }
                                SnackbarController.sendEvent(
                                    event =
                                        SnackbarEvent(
                                            type = SnackbarTypeEnum.ERROR,
                                            message = "Delete order group failed"
                                        )
                                )
                            }
                            is Either.Right -> {
                                _state.update { old -> old.copy(isLoading = false) }
                                _successDialogState.update { OrderSuccessDialogState(transaction) }
                            }
                        }
                    }
                }
            }
        }

        // TODO: Need cloud function later
        private suspend fun decreaseProductItemQuantity(
            orders: List<Order>,
            categories: List<ProductCategory>
        ): Either<String, Pair<List<Product>, Double>> {
            val orderItemUUIDWithQuantityList = orders.associate { it.item?.uuid to it.quantity }
            val soldProduct = mutableListOf<Product>()
            var totalProfit = 0.0
            categories.forEach { category ->
                productItemCheck(
                    orderItemUUIDWithQuantityList = orderItemUUIDWithQuantityList,
                    category = category,
                    addTotalProfit = {
                        totalProfit += it
                    },
                    addSoldProduct = {
                        soldProduct.add(it)
                    }
                ).fold(
                    ifLeft = {
                        return Either.Left(it)
                    },
                    ifRight = { modifiedProducts ->
                        val modifiedCategory = category.copy(products = modifiedProducts)
                        upsertProductCategory(modifiedCategory).isLeft { failure ->
                            return Either.Left(failure.toString())
                        }
                    }
                )
            }
            return Either.Right(soldProduct to totalProfit)
        }

        private fun productItemCheck(
            orderItemUUIDWithQuantityList: Map<String?, Int>,
            category: ProductCategory,
            addTotalProfit: (Double) -> Unit,
            addSoldProduct: (Product) -> Unit
        ): Either<String, List<Product>> {
            val products =
                category.products.map { product ->
                    if (orderItemUUIDWithQuantityList.contains(product.sku)) {
                        var count = orderItemUUIDWithQuantityList[product.sku] ?: 0
                        val soldItems = mutableListOf<ProductItem>()
                        if (product.items.isEmpty()) {
                            return Either.Left("${product.name} is out of stock")
                        }
                        val modifiedItems =
                            product.items.mapNotNull { item ->
                                if (count == 0) return@mapNotNull null
                                val quantity = item.quantity - count
                                when {
                                    quantity > 0 -> {
                                        addTotalProfit(product.getProfit(item, count))
                                        soldItems.add(item.copy(quantity = count))
                                        count = 0
                                        item.copy(quantity = quantity)
                                    }
                                    quantity == 0 -> {
                                        addTotalProfit(product.getProfit(item, count))
                                        soldItems.add(item.copy(quantity = count))
                                        count = 0
                                        null
                                    }
                                    else -> {
                                        return Either.Left(
                                            "${product.name} only has " +
                                                "${item.quantity} items left in stock"
                                        )
                                    }
                                }
                            }
                        addSoldProduct(product.copy(items = soldItems))
                        product.copy(items = modifiedItems)
                    } else {
                        product
                    }
                }
            return Either.Right(products)
        }

        private fun handleSaveForLaterClicked() {
            viewModelScope.launch {
                upsertOrderGroup()
                _navigation.send(CheckOutNavigationTarget.Cart)
            }
        }

        private fun handleBackStackClicked() {
            viewModelScope.launch {
                upsertOrderGroup()
                _navigation.send(CheckOutNavigationTarget.BackStack)
            }
        }

        private fun handleQuantityChanged(
            productUUID: String,
            quantity: Int
        ) {
            _orders.update { old ->
                old.map { order ->
                    if (productUUID == order.item?.uuid) {
                        order.copy(quantity = quantity)
                    } else {
                        order
                    }
                }
            }
        }

        fun onDialogEvent(event: OrderAddOnDialogEvent) {
            when (event) {
                OrderAddOnDialogEvent.Dismiss -> handleDismiss()
                OrderAddOnDialogEvent.Confirm -> handleConfirm()
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

        private fun handleSurchargePercentChanged(value: String) {
            _dialogState.update { old -> old?.copy(surchargePercent = value) }
        }

        private fun handleSurchargeFixedChanged(value: String) {
            _dialogState.update { old -> old?.copy(surchargeFixed = value) }
        }

        private fun handleDiscountPercentChanged(value: String) {
            _dialogState.update { old -> old?.copy(discountPercent = value) }
        }

        private fun handleDiscountFixedChanged(value: String) {
            _dialogState.update { old -> old?.copy(discountFixed = value) }
        }

        private fun handleDismiss() {
            _dialogState.update { null }
        }

        private fun handleConfirm() {
            val dialogState = _dialogState.value
            _state.update { old ->
                old.copy(
                    discount = dialogState?.getDiscount(),
                    surcharge = dialogState?.getSurcharge()
                )
            }
            _dialogState.update { null }
        }

        private suspend fun upsertOrderGroup(): Either<UpsertGroupFailure, Boolean> {
            val orders = _orders.value
            val orderGroup = orderGroup.firstOrNull() ?: return false.right()
            return upsertOrderGroup(orderGroup.copy(orders = orders))
                .map { true }
        }

        fun onSuccessDialogEvent(event: OrderSuccessDialogEvent) {
            when (event) {
                OrderSuccessDialogEvent.Confirm -> handleSuccessConfirm()
            }
        }

        private fun handleSuccessConfirm() {
            viewModelScope.launch {
                _navigation.send(CheckOutNavigationTarget.Cart)
            }
        }
    }