package com.lexwilliam.order.checkout.route

import com.lexwilliam.branch.model.BranchPaymentMethod

sealed interface CheckOutUiEvent {
    data object BackStackClicked : CheckOutUiEvent

    data class QuantityChanged(val productUUID: String, val quantity: Int) : CheckOutUiEvent

    data object ConfirmClicked : CheckOutUiEvent

    data object SaveForLaterClicked : CheckOutUiEvent

    data object PaymentMethodClicked : CheckOutUiEvent

    data object Dismiss : CheckOutUiEvent

    data class PaymentSelected(val payment: BranchPaymentMethod) : CheckOutUiEvent
}