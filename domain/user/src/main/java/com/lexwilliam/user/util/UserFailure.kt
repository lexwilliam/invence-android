package com.lexwilliam.user.util

sealed interface LoginFailure

sealed interface LogoutFailure

data class UnknownFailure(
    val message: String? = null,
) : LoginFailure, LogoutFailure
