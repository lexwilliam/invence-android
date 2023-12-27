package com.lexwilliam.user.session

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Session(
    @SerialName("uuid") val userUUID: String? = null,
)
