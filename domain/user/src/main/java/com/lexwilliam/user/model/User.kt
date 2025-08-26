package com.lexwilliam.user.model

import kotlinx.datetime.Instant

data class User(
    val uuid: String,
    val name: String,
    val imageUrl: String?,
    val email: String = "",
    val createdAt: Instant
)