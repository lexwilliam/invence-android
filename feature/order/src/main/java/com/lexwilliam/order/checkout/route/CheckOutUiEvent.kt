package com.lexwilliam.order.checkout.route

sealed interface CheckOutUiEvent {
    data object BackStackClicked : CheckOutUiEvent

    data class QuantityChanged(val productUUID: String, val quantity: Int) : CheckOutUiEvent

    data object ConfirmClicked : CheckOutUiEvent

    data object SaveForLaterClicked : CheckOutUiEvent
}