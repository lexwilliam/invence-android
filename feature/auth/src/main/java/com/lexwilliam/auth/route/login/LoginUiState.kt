package com.lexwilliam.auth.route.login

import com.lexwilliam.user.model.User

data class LoginUiState(
    val user: User? = null,
    val error: String? = null
) {
    val isUserValid = user != null
}