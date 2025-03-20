package com.lexwilliam.company.model

import com.google.firebase.firestore.PropertyName
import com.lexwilliam.core.util.validateUUID

data class CompanyBranchDto(
    val uuid: String? = null,
    val name: String? = null,
    @JvmField @PropertyName("image_url")
    val imageUrl: String? = null
) {
    fun toDomain(): CompanyBranch =
        CompanyBranch(
            uuid = uuid.validateUUID(),
            name = name ?: "",
            imageUrl = imageUrl
        )

    companion object {
        fun fromDomain(domain: CompanyBranch): CompanyBranchDto =
            CompanyBranchDto(
                uuid = domain.uuid.toString(),
                name = domain.name,
                imageUrl = domain.imageUrl
            )
    }
}