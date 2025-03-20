package com.lexwilliam.branch.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.lexwilliam.firebase.extensions.toKtxInstant
import com.lexwilliam.firebase.extensions.toTimestamp
import kotlinx.datetime.Instant
import java.util.UUID

data class BranchDto(
    val uuid: String? = null,
    val name: String? = null,
    @JvmField @PropertyName("logo_url")
    val logoUrl: String? = null,
    val address: String? = null,
    val phone: String? = null,
    @JvmField @PropertyName("created_at")
    val createdAt: Timestamp? = null
) {
    fun toDomain(): Branch =
        Branch(
            uuid = uuid?.let { UUID.fromString(uuid) } ?: UUID.randomUUID(),
            name = name ?: "",
            logoUrl = logoUrl ?: "",
            createdAt = createdAt?.toKtxInstant() ?: Instant.DISTANT_PAST
        )

    companion object {
        fun fromDomain(domain: Branch): BranchDto =
            BranchDto(
                uuid = domain.uuid.toString(),
                name = domain.name,
                logoUrl = domain.logoUrl.toString(),
                createdAt = domain.createdAt.toTimestamp()
            )
    }
}