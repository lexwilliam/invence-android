package com.lexwilliam.auth.route.login

data class LoginState(
    val isSuccessful: Boolean = false,
    val error: String? = null,
)
