package com.lexwilliam.auth.route.login

sealed interface LoginUiEvent {
    object LoginTapped : LoginUiEvent
}
