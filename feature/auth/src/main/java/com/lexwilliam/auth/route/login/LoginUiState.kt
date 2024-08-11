package com.lexwilliam.auth.route.login

import com.lexwilliam.user.model.User

data class LoginUiState(
    val user: User? = null,
    val email: String = "",
    val password: String = "",
    val isPasswordShowing: Boolean = false,
    val error: String? = null
) {
    val isUserValid = user != null
}