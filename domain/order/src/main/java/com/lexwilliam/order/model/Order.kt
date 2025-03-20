package com.lexwilliam.order.model

import kotlinx.datetime.Instant
import java.util.UUID

data class Order(
    val uuid: UUID,
    val item: OrderItem?,
    val discounts: List<OrderDiscount>,
    val quantity: Int,
    val refundedQuantity: Int,
    val note: String,
    val createdAt: Instant,
    val updatedAt: Instant? = null,
    val deletedAt: Instant? = null
)