package com.lexwilliam.auth.route.login.route

import com.lexwilliam.auth.util.SignInResult

sealed interface LoginUiEvent {
    data class SignInWithGoogle(val result: SignInResult) : LoginUiEvent

    data object SignInClicked : LoginUiEvent

    data class EmailChanged(val value: String) : LoginUiEvent

    data class PasswordChanged(val value: String) : LoginUiEvent

    data object PasswordVisibilityChanged : LoginUiEvent

    data object SignUpClicked : LoginUiEvent

    data object ForgotPasswordClicked : LoginUiEvent
}