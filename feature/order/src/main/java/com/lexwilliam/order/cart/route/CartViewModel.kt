package com.lexwilliam.order.cart.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexwilliam.core_ui.controller.SnackbarController
import com.lexwilliam.core_ui.controller.SnackbarEvent
import com.lexwilliam.core_ui.model.SnackbarTypeEnum
import com.lexwilliam.order.cart.navigation.CartNavigationTarget
import com.lexwilliam.order.model.OrderGroup
import com.lexwilliam.order.usecase.DeleteOrderGroupUseCase
import com.lexwilliam.order.usecase.ObserveOrderGroupUseCase
import com.lexwilliam.order.usecase.UpsertOrderGroupUseCase
import com.lexwilliam.user.usecase.FetchCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CartViewModel
    @Inject
    constructor(
        observeOrderGroup: ObserveOrderGroupUseCase,
        private val upsertOrderGroup: UpsertOrderGroupUseCase,
        private val deleteOrderGroup: DeleteOrderGroupUseCase,
        private val fetchCurrentUser: FetchCurrentUserUseCase
    ) : ViewModel() {
        private val _navigation = Channel<CartNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        private val _isLoading = MutableStateFlow(false)
        val isLoading = _isLoading.asStateFlow()

        val orderGroup =
            observeOrderGroup()
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5_000),
                    emptyList()
                )

        fun onEvent(event: CartUiEvent) {
            when (event) {
                CartUiEvent.AddCartClicked -> handleAddCartClicked()
                is CartUiEvent.RemoveCartClicked -> handleRemoveCartClicked(event.orderGroup)
                is CartUiEvent.CartClicked -> handleCartClicked(event.orderGroup)
            }
        }

        private fun handleRemoveCartClicked(order: OrderGroup) {
            viewModelScope.launch {
                deleteOrderGroup(order).fold(
                    ifLeft = { failure ->
                        SnackbarController.sendEvent(
                            event =
                                SnackbarEvent(
                                    type = SnackbarTypeEnum.ERROR,
                                    message = "Delete Order Group Failed"
                                )
                        )
                    },
                    ifRight = {
                        SnackbarController.sendEvent(
                            event =
                                SnackbarEvent(
                                    type = SnackbarTypeEnum.SUCCESS,
                                    message = "${order.uuid} Order Group Deleted"
                                )
                        )
                    }
                )
            }
        }

        private fun handleAddCartClicked() {
            _isLoading.value = true
            viewModelScope.launch {
                val user = fetchCurrentUser().getOrNull() ?: return@launch
                val orderGroup =
                    OrderGroup(
                        uuid = UUID.randomUUID(),
                        userUUID = user.uuid,
                        createdBy = user.name,
                        orders = emptyList(),
                        // TODO: Apply taxes and discount after implemented
                        taxes = emptyList(),
                        discounts = emptyList(),
                        createdAt = Clock.System.now()
                    )
                upsertOrderGroup(orderGroup).fold(
                    ifLeft = { failure ->
                        SnackbarController.sendEvent(
                            event =
                                SnackbarEvent(
                                    type = SnackbarTypeEnum.ERROR,
                                    message = "Upsert Order Group Failed"
                                )
                        )
                        _isLoading.value = false
                    },
                    ifRight = {
                    }
                )
            }
        }

        private fun handleCartClicked(orderGroup: OrderGroup) {
            viewModelScope.launch {
                _navigation.send(CartNavigationTarget.Order(orderGroup.uuid))
            }
        }
    }