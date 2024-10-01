package com.lexwilliam.order.checkout.dialog

import com.lexwilliam.order.model.OrderDiscount
import com.lexwilliam.order.model.OrderTax
import java.util.UUID

data class OrderAddOnDialogState(
    val discountFixed: String = "",
    val discountPercent: String = "",
    val surchargeFixed: String = "",
    val surchargePercent: String = ""
) {
    fun getDiscount(): OrderDiscount? {
        return if (discountFixed.isNotBlank() || discountPercent.isNotBlank()) {
            OrderDiscount(
                uuid = UUID.randomUUID(),
                name = "Discount",
                fixed = discountFixed.toDoubleOrNull() ?: 0.0,
                percent = (discountPercent.toFloatOrNull() ?: 0.0f) / 100
            )
        } else {
            null
        }
    }

    fun getSurcharge(): OrderTax? {
        return if (surchargeFixed.isNotBlank() || surchargePercent.isNotBlank()) {
            OrderTax(
                uuid = UUID.randomUUID(),
                name = "Surcharge",
                fixed = surchargeFixed.toDoubleOrNull() ?: 0.0,
                percent = (surchargePercent.toFloatOrNull() ?: 0.0f) / 100
            )
        } else {
            null
        }
    }

    companion object {
        fun from(
            discount: OrderDiscount?,
            surcharge: OrderTax?
        ): OrderAddOnDialogState {
            return OrderAddOnDialogState(
                discountFixed = discount?.fixed?.toString() ?: "",
                discountPercent = discount?.let { it.percent * 100.0f }?.toString() ?: "",
                surchargeFixed = surcharge?.fixed?.toString() ?: "",
                surchargePercent = surcharge?.let { it.percent * 100.0f }?.toString() ?: ""
            )
        }
    }
}