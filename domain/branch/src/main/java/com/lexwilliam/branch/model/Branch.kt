package com.lexwilliam.branch.model

import android.net.Uri
import kotlinx.datetime.Instant
import java.util.UUID

data class Branch(
    val uuid: UUID,
    val name: String,
    val logoUrl: Uri?,
    val createdAt: Instant
)