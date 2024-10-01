package com.lexwilliam.auth.route.login.route

import com.lexwilliam.core.util.validateEmail

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordShowing: Boolean = false,
    val error: String? = null
) {
    val isValid = email.isNotBlank() && password.isNotBlank() && validateEmail(email)
}