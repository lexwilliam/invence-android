package com.lexwilliam.transaction.model

import com.lexwilliam.order.model.OrderGroup
import kotlinx.datetime.Instant
import java.util.UUID

data class Transaction(
    val uuid: UUID,
    val branchUUID: UUID,
    val orderGroup: OrderGroup,
    val profit: Double,
    val customer: String,
    val createdBy: String,
    val createdAt: Instant,
    val deletedAt: Instant? = null
) {
    val total = orderGroup.totalPrice
}