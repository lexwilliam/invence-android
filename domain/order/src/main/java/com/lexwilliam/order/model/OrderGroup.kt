package com.lexwilliam.order.model

import kotlinx.datetime.Instant
import java.util.UUID

data class OrderGroup(
    val uuid: UUID = UUID.randomUUID(),
    val userUUID: String,
    val createdBy: String = "",
    val orders: List<Order> = emptyList(),
    val taxes: List<OrderTax> = emptyList(),
    val discounts: List<OrderDiscount> = emptyList(),
    val createdAt: Instant = Instant.DISTANT_PAST,
    val deletedAt: Instant? = null,
    val completedAt: Instant? = null
) {
    val totalPrice = orders.sumOf { order -> order.quantity * ((order.item?.price) ?: 0.0) }
}