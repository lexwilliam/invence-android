package com.lexwilliam.transaction.model

import kotlinx.datetime.Instant
import java.util.UUID

data class Refund(
    val uuid: UUID,
    val total: Double,
    val refundedBy: String,
    val reason: String,
    val createdAt: Instant,
    val deletedAt: Instant? = null
)
