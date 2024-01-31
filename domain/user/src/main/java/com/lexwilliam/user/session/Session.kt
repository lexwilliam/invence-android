package com.lexwilliam.user.session

import kotlinx.serialization.SerialName

data class Session(
    @SerialName("uuid") val userUUID: String? = null,
    @SerialName("branch_uuid") val branchUUID: String? = null
)