package com.lexwilliam.order.model

import kotlinx.datetime.Instant
import java.util.UUID

data class OrderGroup(
    val uuid: UUID,
    val branchUUID: UUID,
    val createdBy: String,
    val orders: List<Order>,
    val taxes: List<OrderTax>,
    val discounts: List<OrderDiscount>,
    val createdAt: Instant,
    val deletedAt: Instant? = null,
    val completedAt: Instant? = null
) {
    val totalPrice = orders.sumOf { order -> order.quantity * order.item.price }
}