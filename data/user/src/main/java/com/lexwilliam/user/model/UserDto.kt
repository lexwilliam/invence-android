package com.lexwilliam.user.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.lexwilliam.firebase.extensions.toKtxInstant
import com.lexwilliam.firebase.extensions.toTimestamp
import kotlinx.datetime.Instant
import java.util.UUID

data class UserDto(
    val uuid: String? = null,
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
            branchUUID = branchUUID?.let { UUID.fromString(it) },
            name = name ?: "",
            imageUrl = imageUrl,
            email = email ?: "",
            role = Role.entries.firstOrNull { it.title == role },
            createdAt = createdAt?.toKtxInstant() ?: Instant.DISTANT_PAST
        )

    companion object {
        fun fromDomain(domain: User): UserDto =
            UserDto(
                uuid = domain.uuid,
                branchUUID = domain.branchUUID?.toString(),
                name = domain.name,
                imageUrl = domain.imageUrl,
                email = domain.email,
                role = domain.role?.title,
                createdAt = domain.createdAt.toTimestamp()
            )
    }
}