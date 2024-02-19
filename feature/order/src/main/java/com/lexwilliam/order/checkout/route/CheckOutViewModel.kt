package com.lexwilliam.order.checkout.route

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import arrow.core.right
import com.lexwilliam.branch.model.BranchPaymentMethod
import com.lexwilliam.branch.usecase.ObserveBranchUseCase
import com.lexwilliam.order.checkout.navigation.CheckOutNavigationTarget
import com.lexwilliam.order.model.Order
import com.lexwilliam.order.usecase.ObserveSingleOrderGroupUseCase
import com.lexwilliam.order.usecase.UpsertOrderGroupUseCase
import com.lexwilliam.order.util.UpsertGroupFailure
import com.lexwilliam.transaction.model.Payment
import com.lexwilliam.transaction.model.PaymentMethod
import com.lexwilliam.transaction.model.PaymentMethodFee
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
        private val upsertTransaction: UpsertTransactionUseCase,
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

        private val branch = branchUUID.flatMapLatest { uuid ->
            when (uuid) {
                null -> flowOf(null)
                else -> observeBranch(uuid)
            }
        }

        private val paymentMethods = branch.map { it?.branchPaymentMethods ?: emptyList() }

        private val _selectedPayMethod = MutableStateFlow<BranchPaymentMethod?>(null)
        val selectedPayMethod = _selectedPayMethod.asStateFlow()

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
            }
        }

        private fun handleConfirmClicked() {
            viewModelScope.launch {
                val orderGroup = orderGroup.firstOrNull() ?: return@launch
                val branchUUID = branchUUID.firstOrNull() ?: return@launch
                val selectedPayMethod = _selectedPayMethod.value ?: return@launch
                val user = user.firstOrNull() ?: return@launch
                val orders = _orders.value
                val paymentMethod = PaymentMethod(
                    uuid = selectedPayMethod.uuid,
                    group = selectedPayMethod.group,
                    name = selectedPayMethod.name,
                    fee = PaymentMethodFee(
                        fixed = selectedPayMethod.fee?.fixed ?: 0.0,
                        percent = selectedPayMethod.fee?.percent ?: 0.0f
                    )
                )
                val payment = Payment(
                    uuid = UUID.randomUUID(),
                    total = orderGroup.totalPrice,
                    paymentMethod = paymentMethod,
                    createdAt = Clock.System.now()
                )
                val transactionUUID = UUID.randomUUID()
                val transaction = Transaction(
                    uuid = transactionUUID,
                    branchUUID = branchUUID,
                    orderGroup = orderGroup.copy(orders = orders),
                    payments = listOf(payment),
                    refunds = listOf(),
                    customer = "",
                    fee = _transactionFee.value,
                    // TODO: Implement Tip Later
                    tip = 0.0,
                    createdBy = user.name,
                    createdAt = Clock.System.now()
                )
                when (val result = upsertTransaction(transaction)) {
                    is Either.Left -> {
                        Log.d("TAG", result.value.toString())
                    }
                    is Either.Right -> {
                        _navigation
                            .send(CheckOutNavigationTarget.TransactionDetail(transactionUUID))
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

        private suspend fun upsertOrderGroup(): Either<UpsertGroupFailure, Boolean> {
            val orders = _orders.value
            val orderGroup = orderGroup.firstOrNull() ?: return false.right()
            return upsertOrderGroup(orderGroup.copy(orders = orders))
                .map { true }
        }
    }