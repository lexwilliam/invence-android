package com.lexwilliam.auth.route.signup.route

import com.lexwilliam.core.util.validateEmail

data class SignUpUiState(
    val email: String = "",
    val name: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val error: String? = null,
    val isPasswordShowing: Boolean = false,
    val isConfirmPasswordShowing: Boolean = false,
    val isLoading: Boolean = false
) {
    val isValid =
        email.isNotBlank() &&
            validateEmail(email) &&
            name.isNotBlank() &&
            password.isNotBlank() &&
            confirmPassword.isNotBlank() &&
            password == confirmPassword
}