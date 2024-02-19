package com.lexwilliam.transaction.model

import kotlinx.datetime.Instant
import java.util.UUID

data class Payment(
    val uuid: UUID,
    val total: Double,
    val paymentMethod: PaymentMethod,
    val createdAt: Instant,
    val deletedAt: Instant? = null
)