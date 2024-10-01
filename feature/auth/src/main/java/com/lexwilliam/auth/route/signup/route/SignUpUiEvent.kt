package com.lexwilliam.auth.route.signup.route

sealed interface SignUpUiEvent {
    data object BackClicked : SignUpUiEvent

    data class EmailChanged(val email: String) : SignUpUiEvent

    data class NameChanged(val name: String) : SignUpUiEvent

    data class PasswordChanged(val password: String) : SignUpUiEvent

    data class PasswordConfirmChanged(val password: String) : SignUpUiEvent

    data object PasswordVisibilityChanged : SignUpUiEvent

    data object ConfirmPasswordVisibilityChanged : SignUpUiEvent

    data object SignUpClicked : SignUpUiEvent
}