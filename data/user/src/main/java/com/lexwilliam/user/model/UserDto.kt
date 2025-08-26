package com.lexwilliam.user.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.lexwilliam.firebase.extensions.toKtxInstant
import com.lexwilliam.firebase.extensions.toTimestamp
import kotlinx.datetime.Instant

data class UserDto(
    val uuid: String? = null,
    @JvmField @PropertyName("company_uuid")
    val companyUUID: String? = null,
    @JvmField @PropertyName("branch_uuid")
    val branchUUID: String? = null,
    val name: String? = null,
    @JvmField @PropertyName("image_url")
    val imageUrl: String? = null,
    val email: String? = null,
    val role: String? = null,
    @JvmField @PropertyName("created_at")
    val createdAt: Timestamp? = null
) {
    fun toDomain(): User =
        User(
            uuid = uuid ?: "",
            name = name ?: "",
            imageUrl = imageUrl,
            email = email ?: "",
            createdAt = createdAt?.toKtxInstant() ?: Instant.DISTANT_PAST
        )

    companion object {
        fun fromDomain(domain: User): UserDto =
            UserDto(
                uuid = domain.uuid,
                name = domain.name,
                imageUrl = domain.imageUrl,
                email = domain.email,
                createdAt = domain.createdAt.toTimestamp()
            )
    }
}