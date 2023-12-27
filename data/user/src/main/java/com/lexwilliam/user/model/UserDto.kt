package com.lexwilliam.user.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class UserDto(
    val uuid: String? = null,
    @JvmField @PropertyName("branch_uuid")
    val branchUUID: String? = null,
    val name: String? = null,
    val email: String? = null,
    @JvmField @PropertyName("created_at")
    val createdAt: Timestamp? = null,
)
