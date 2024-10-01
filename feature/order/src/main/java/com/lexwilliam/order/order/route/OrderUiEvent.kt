package com.lexwilliam.order.order.route

import com.lexwilliam.product.model.Product

sealed interface OrderUiEvent {
    data object BackStackClicked : OrderUiEvent

    data class QueryChanged(val value: String) : OrderUiEvent

    data object BarcodeScannerClicked : OrderUiEvent

    data object CheckOutClicked : OrderUiEvent

    data class QuantityChanged(val product: Product, val quantity: Int) : OrderUiEvent
}