package com.lexwilliam.order.checkout.dialog

sealed interface OrderAddOnDialogEvent {
    data object Dismiss : OrderAddOnDialogEvent

    data object Confirm : OrderAddOnDialogEvent

    data class DiscountFixedChanged(val value: String) : OrderAddOnDialogEvent

    data class DiscountPercentChanged(val value: String) : OrderAddOnDialogEvent

    data class SurchargeFixedChanged(val value: String) : OrderAddOnDialogEvent

    data class SurchargePercentChanged(val value: String) : OrderAddOnDialogEvent
}