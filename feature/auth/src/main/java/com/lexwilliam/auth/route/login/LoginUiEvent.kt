package com.lexwilliam.auth.route.login

import com.lexwilliam.auth.util.SignInResult

sealed interface LoginUiEvent {
    data object UserAlreadyAuthenticated : LoginUiEvent

    data class SignIn(val result: SignInResult) : LoginUiEvent

    data object Success : LoginUiEvent
}