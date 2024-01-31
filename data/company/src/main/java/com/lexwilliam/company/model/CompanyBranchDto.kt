package com.lexwilliam.company.model

import com.lexwilliam.core.util.validateUUID

data class CompanyBranchDto(
    val uuid: String? = null,
    val name: String? = null
) {
    fun toDomain(): CompanyBranch = CompanyBranch(
        uuid = uuid.validateUUID(),
        name = name ?: ""
    )

    companion object {
        fun fromDomain(domain: CompanyBranch): CompanyBranchDto = CompanyBranchDto(
            uuid = domain.uuid.toString(),
            name = domain.name
        )
    }
}