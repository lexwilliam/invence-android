package com.lexwilliam.branch.model

import androidx.core.net.toUri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.lexwilliam.firebase.toKtxInstant
import com.lexwilliam.firebase.toTimestamp
import kotlinx.datetime.Instant
import java.util.UUID

data class BranchDto(
    val uuid: String? = null,
    val name: String? = null,
    @JvmField @PropertyName("logo_url")
    val logoUrl: String? = null,
    val address: String? = null,
    val phone: String? = null,
    val paymentMethods: Map<String, PaymentMethodDto>? = null,
    @JvmField @PropertyName("created_at")
    val createdAt: Timestamp? = null
) {
    fun toDomain(): Branch =
        Branch(
            uuid = uuid?.let { UUID.fromString(uuid) } ?: UUID.randomUUID(),
            name = name ?: "",
            logoUrl = logoUrl?.toUri(),
            branchPaymentMethods = paymentMethods?.values?.map { it.toDomain() } ?: emptyList(),
            createdAt = createdAt?.toKtxInstant() ?: Instant.DISTANT_PAST
        )

    companion object {
        fun fromDomain(domain: Branch): BranchDto =
            BranchDto(
                uuid = domain.uuid.toString(),
                name = domain.name,
                logoUrl = domain.logoUrl.toString(),
                paymentMethods = domain.branchPaymentMethods
                    .associate { it.uuid.toString() to PaymentMethodDto.fromDomain(it) },
                createdAt = domain.createdAt.toTimestamp()
            )
    }
}