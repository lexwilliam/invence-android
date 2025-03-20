package com.lexwilliam.branch.model

import kotlinx.datetime.Instant
import java.util.UUID

data class Branch(
    val uuid: UUID,
    val name: String,
    val logoUrl: String?,
    val createdAt: Instant
)