package com.lexwilliam.company.model

import kotlinx.datetime.Instant

data class CompanyInviteRequest(
    val userId: String,
    val email: String,
    val imageUrl: String?,
    val createdAt: Instant
)