package com.lexwilliam.core.model

import java.util.UUID

data class SnackbarMessage(
    val id: UUID = UUID.randomUUID(),
    val message: String = "",
    val action: (() -> Unit)? = null,
    val actionLabel: String? = null
)