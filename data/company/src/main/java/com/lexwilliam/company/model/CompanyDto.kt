package com.lexwilliam.company.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.lexwilliam.firebase.extensions.toKtxInstant
import com.lexwilliam.firebase.extensions.toTimestamp
import kotlinx.datetime.Instant

data class CompanyDto(
    val uuid: String? = null,
    val name: String? = null,
    @JvmField @PropertyName("logo_url")
    val logoUrl: String? = null,
    val branches: List<CompanyBranchDto>? = null,
    val inviteRequest: List<CompanyInviteRequestDto>? = null,
    @JvmField @PropertyName("created_at")
    val createdAt: Timestamp? = null
) {
    fun toDomain(): Company =
        Company(
            uuid = uuid ?: "",
            name = name ?: "",
            logoUrl = logoUrl,
            branches = branches?.map { branch -> branch.toDomain() } ?: emptyList(),
            inviteRequest = inviteRequest?.map { request -> request.toDomain() } ?: emptyList(),
            createdAt = createdAt?.toKtxInstant() ?: Instant.DISTANT_PAST
        )

    companion object {
        fun fromDomain(domain: Company): CompanyDto =
            CompanyDto(
                uuid = domain.uuid,
                name = domain.name,
                logoUrl = domain.logoUrl,
                branches = domain.branches.map { branch -> CompanyBranchDto.fromDomain(branch) },
                inviteRequest =
                    domain.inviteRequest.map {
                            request ->
                        CompanyInviteRequestDto.fromDomain(request)
                    },
                createdAt = domain.createdAt.toTimestamp()
            )
    }
}