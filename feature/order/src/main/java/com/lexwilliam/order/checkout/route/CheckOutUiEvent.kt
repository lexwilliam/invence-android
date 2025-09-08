package com.lexwilliam.order.checkout.route

import com.lexwilliam.order.model.OrderDiscount
import com.lexwilliam.order.model.OrderTax

sealed interface CheckOutUiEvent {
    data object BackStackClicked : CheckOutUiEvent

    data class QuantityChanged(val productUUID: String, val quantity: Int) : CheckOutUiEvent

    data object ConfirmClicked : CheckOutUiEvent

    data object SaveForLaterClicked : CheckOutUiEvent

    data object Dismiss : CheckOutUiEvent

    data class AddOnClicked(
        val discount: OrderDiscount? = null,
        val surcharge: OrderTax? = null
    ) : CheckOutUiEvent
}