package com.lexwilliam.order.model

import java.util.UUID

data class OrderDiscount(
    val uuid: UUID,
    val name: String,
    val fixed: Double,
    val percent: Float
) {
    fun calculate(total: Double): Double {
        return (fixed + total * percent) * -1
    }
}