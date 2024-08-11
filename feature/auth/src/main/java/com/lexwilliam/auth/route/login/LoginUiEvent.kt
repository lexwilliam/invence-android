package com.lexwilliam.auth.route.login

import com.lexwilliam.auth.util.SignInResult

sealed interface LoginUiEvent {
    data class SignInWithGoogle(val result: SignInResult) : LoginUiEvent

    data class EmailChanged(val value: String) : LoginUiEvent

    data class PasswordChanged(val value: String) : LoginUiEvent
}