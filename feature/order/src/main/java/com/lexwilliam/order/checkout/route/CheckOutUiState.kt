package com.lexwilliam.order.checkout.route

import com.lexwilliam.order.model.OrderDiscount
import com.lexwilliam.order.model.OrderTax

data class CheckOutUiState(
    val discount: OrderDiscount? = null,
    val surcharge: OrderTax? = null,
    val isLoading: Boolean = false
) {
    fun calculateTotal(total: Double): Double {
        return total + (discount?.calculate(total) ?: 0.0) + (surcharge?.calculate(total) ?: 0.0)
    }
}