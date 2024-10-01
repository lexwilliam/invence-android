package com.lexwilliam.order.cart.route

import com.lexwilliam.order.model.OrderGroup

sealed interface CartUiEvent {
    data object BackStackClicked : CartUiEvent

    data object AddCartClicked : CartUiEvent

    data class RemoveCartClicked(val orderGroup: OrderGroup) : CartUiEvent

    data class CartClicked(val orderGroup: OrderGroup) : CartUiEvent
}