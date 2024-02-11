package com.lexwilliam.product.route.detail.dialog

sealed interface RestockDialogEvent {
    data class BuyPriceChanged(val value: String) : RestockDialogEvent

    data class QuantityChanged(val value: String) : RestockDialogEvent

    data object Dismiss : RestockDialogEvent

    data object Confirm : RestockDialogEvent
}