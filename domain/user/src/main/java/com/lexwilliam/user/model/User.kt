package com.lexwilliam.user.model

import android.net.Uri
import kotlinx.datetime.Instant
import java.util.UUID

data class User(
    val uuid: String,
    val branchUUID: UUID? = null,
    val name: String,
    val imageUrl: Uri?,
    val email: String = "",
    val createdAt: Instant
)