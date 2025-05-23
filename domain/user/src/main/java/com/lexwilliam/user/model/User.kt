package com.lexwilliam.user.model

import kotlinx.datetime.Instant
import java.util.UUID

data class User(
    val uuid: String,
    val companyUUID: UUID?,
    val branchUUID: UUID? = null,
    val name: String,
    val imageUrl: String?,
    val email: String = "",
    val role: Role?,
    val createdAt: Instant
)