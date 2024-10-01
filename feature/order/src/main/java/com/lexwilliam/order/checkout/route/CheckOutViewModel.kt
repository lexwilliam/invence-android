package com.lexwilliam.order.checkout.route

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import arrow.core.right
import com.lexwilliam.branch.usecase.ObserveBranchUseCase
import com.lexwilliam.order.checkout.dialog.OrderAddOnDialogEvent
import com.lexwilliam.order.checkout.dialog.OrderAddOnDialogState
import com.lexwilliam.order.checkout.navigation.CheckOutNavigationTarget
import com.lexwilliam.order.model.Order
import com.lexwilliam.order.model.OrderDiscount
import com.lexwilliam.order.model.OrderTax
import com.lexwilliam.order.usecase.DeleteOrderGroupUseCase
import com.lexwilliam.order.usecase.ObserveSingleOrderGroupUseCase
import com.lexwilliam.order.usecase.UpsertOrderGroupUseCase
import com.lexwilliam.order.util.UpsertGroupFailure
import com.lexwilliam.product.usecase.ObserveProductCategoryUseCase
import com.lexwilliam.product.usecase.UpsertProductCategoryUseCase
import com.lexwilliam.transaction.model.Transaction
import com.lexwilliam.transaction.model.TransactionFee
import com.lexwilliam.transaction.usecase.UpsertTransactionUseCase
import com.lexwilliam.user.usecase.FetchUserUseCase
import com.lexwilliam.user.usecase.ObserveSessionUseCase
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
        private val observeProductCategory: ObserveProductCategoryUseCase,
        private val upsertProductCategory: UpsertProductCategoryUseCase,
        private val fetchUser: FetchUserUseCase,
        observeSession: ObserveSessionUseCase,
        observeBranch: ObserveBranchUseCase,
        savedStateHandle: SavedStateHandle
    ) : ViewModel() {
        private val orderUUID =
            savedStateHandle
                .getStateFlow<String?>("orderUUID", null)
                .map { uuid -> uuid?.let { UUID.fromString(it) } }

        private val _navigation = Channel<CheckOutNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        private val user =
            observeSession().map { session ->
                session.userUUID
                    ?.let { fetchUser(it) }
                    ?.getOrNull()
            }

        private val branchUUID = user.map { it?.branchUUID }

        private val branch =
            branchUUID.flatMapLatest { uuid ->
                when (uuid) {
                    null -> flowOf(null)
                    else -> observeBranch(uuid)
                }
            }

        private val categories =
            branchUUID.flatMapLatest {
                when (it) {
                    null -> flowOf(emptyList())
                    else -> observeProductCategory(it)
                }
            }

        private val _state = MutableStateFlow(CheckOutUiState())
        val state = _state.asStateFlow()

        private val _dialogState = MutableStateFlow<OrderAddOnDialogState?>(null)
        val dialogState = _dialogState.asStateFlow()

        private val _transactionFee = MutableStateFlow<TransactionFee?>(null)
        val transactionFee = _transactionFee.asStateFlow()

        private val orderGroup =
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
            viewModelScope.launch {
                val orderGroup = orderGroup.firstOrNull() ?: return@launch
                val branchUUID = branchUUID.firstOrNull() ?: return@launch
                val user = user.firstOrNull() ?: return@launch
                val categories = categories.firstOrNull() ?: return@launch
                val orders = _orders.value
                val transactionUUID = UUID.randomUUID()
                val transaction =
                    Transaction(
                        uuid = transactionUUID,
                        branchUUID = branchUUID,
                        orderGroup = orderGroup.copy(orders = orders),
                        payments = listOf(),
                        refunds = listOf(),
                        customer = "",
                        // TODO: Implement Tip Later
                        tip = 0.0,
                        createdBy = user.name,
                        createdAt = Clock.System.now()
                    )

                when (val resultTransaction = upsertTransaction(transaction)) {
                    is Either.Left -> {
                        Log.d("TAG", resultTransaction.value.toString())
                    }
                    is Either.Right -> {
                        when (val result = deleteOrderGroup(orderGroup)) {
                            is Either.Left -> {
                                Log.d("TAG", result.value.toString())
                            }
                            is Either.Right -> {
                                _navigation
                                    .send(
                                        CheckOutNavigationTarget.TransactionDetail(transactionUUID)
                                    )
                            }
                        }
                    }
                }
            }
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
                    if (productUUID == order.item.uuid) {
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
    }