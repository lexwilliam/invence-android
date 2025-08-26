package com.lexwilliam.core.session

sealed class Session {
    data object Unauthenticated : Session()

    data class Authenticated(
        val userUUID: String? = null
    ) : Session()

    fun getUserId(): String? =
        when (this) {
            is Authenticated -> userUUID
            else -> null
        }
}