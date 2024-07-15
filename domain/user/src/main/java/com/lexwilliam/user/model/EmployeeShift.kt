package com.lexwilliam.user.model

import java.util.UUID

data class EmployeeShift(
    val userUUID: String = "",
    val branchUUID: UUID = UUID.randomUUID(),
    val username: String = "",
    val shift: List<String> = emptyList()
)