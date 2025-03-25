package com.lexwilliam.company.model

import com.google.firebase.Timestamp
import com.lexwilliam.firebase.extensions.toKtxInstant
import com.lexwilliam.firebase.extensions.toTimestamp
import kotlinx.datetime.Instant

data class CompanyInviteRequestDto(
    val userId: String? = null,
    val email: String? = null,
    val imageUrl: String? = null,
    val createdAt: Timestamp? = null
) {
    fun toDomain(): CompanyInviteRequest =
        CompanyInviteRequest(
            userId = userId ?: "",
            email = email ?: "",
            imageUrl = imageUrl,
            createdAt = createdAt?.toKtxInstant() ?: Instant.DISTANT_PAST
        )

    companion object {
        fun fromDomain(domain: CompanyInviteRequest): CompanyInviteRequestDto =
            CompanyInviteRequestDto(
                userId = domain.userId,
                email = domain.email,
                imageUrl = domain.imageUrl,
                createdAt = domain.createdAt.toTimestamp()
            )
    }
}