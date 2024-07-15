package com.lexwilliam.user.model

import com.google.firebase.firestore.PropertyName
import com.lexwilliam.core.util.validateUUID

data class EmployeeShiftDto(
    @JvmField @PropertyName("user_uuid")
    val userUUID: String? = null,
    @JvmField @PropertyName("branch_uuid")
    val branchUUID: String? = null,
    val username: String? = null,
    val shift: List<String>? = null
) {
    fun toDomain() =
        EmployeeShift(
            userUUID = userUUID ?: "",
            branchUUID = branchUUID.validateUUID(),
            username = username ?: "",
            shift = shift ?: emptyList()
        )

    companion object {
        fun fromDomain(domain: EmployeeShift) =
            EmployeeShiftDto(
                userUUID = domain.userUUID,
                branchUUID = domain.branchUUID.toString(),
                username = domain.username,
                shift = domain.shift
            )
    }
}