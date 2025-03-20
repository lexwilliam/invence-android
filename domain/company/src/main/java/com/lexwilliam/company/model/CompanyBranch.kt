package com.lexwilliam.company.model

import java.util.UUID

data class CompanyBranch(
    val uuid: UUID,
    val name: String,
    val imageUrl: String?
)