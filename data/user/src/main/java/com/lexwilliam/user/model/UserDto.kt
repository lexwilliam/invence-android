package com.lexwilliam.user.model

import androidx.core.net.toUri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.lexwilliam.core.util.validateUUID
import com.lexwilliam.firebase.toKtxInstant
import com.lexwilliam.firebase.toTimestamp
import kotlinx.datetime.Instant

data class UserDto(
    val uuid: String? = null,
    @JvmField @PropertyName("branch_uuid")
    val branchUUID: String? = null,
    val name: String? = null,
    val imageUrl: String? = null,
    val email: String? = null,
    @JvmField @PropertyName("created_at")
    val createdAt: Timestamp? = null
) {
    fun toDomain(): User =
        User(
            uuid = uuid ?: "",
            branchUUID = branchUUID.validateUUID(),
            name = name ?: "",
            imageUrl = imageUrl?.toUri(),
            email = email ?: "",
            createdAt = createdAt?.toKtxInstant() ?: Instant.DISTANT_PAST
        )

    companion object {
        fun fromDomain(domain: User): UserDto =
            UserDto(
                uuid = domain.uuid.toString(),
                branchUUID = domain.branchUUID.toString(),
                name = domain.name,
                imageUrl = domain.imageUrl?.toString(),
                email = domain.email,
                createdAt = domain.createdAt.toTimestamp()
            )
    }
}