package com.lexwilliam.company.model

import kotlinx.datetime.Instant
import java.util.UUID

data class Company(
    val uuid: String,
    val name: String,
    val logoUrl: String?,
    val branches: List<CompanyBranch>,
    val createdAt: Instant
)