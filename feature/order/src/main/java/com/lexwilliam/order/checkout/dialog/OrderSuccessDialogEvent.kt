package com.lexwilliam.order.checkout.dialog

sealed class OrderSuccessDialogEvent {
    object Confirm : OrderSuccessDialogEvent()
}