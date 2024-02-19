package com.lexwilliam.transaction.model

import com.lexwilliam.order.model.OrderGroup
import kotlinx.datetime.Instant
import java.util.UUID

data class Transaction(
    val uuid: UUID,
    val branchUUID: UUID,
    val orderGroup: OrderGroup,
    val payments: List<Payment>,
    val refunds: List<Refund>,
    val customer: String,
    val fee: TransactionFee?,
    val tip: Double,
    val createdBy: String,
    val createdAt: Instant,
    val deletedAt: Instant? = null
)