package com.lexwilliam.company.model

import kotlinx.datetime.Instant

data class Company(
    val uuid: String,
    val name: String,
    val logoUrl: String?,
    val branches: List<CompanyBranch>,
    val inviteRequest: List<CompanyInviteRequest>,
    val createdAt: Instant
)