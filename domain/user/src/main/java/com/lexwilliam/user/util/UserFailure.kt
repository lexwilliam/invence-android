package com.lexwilliam.user.util

sealed interface LoginFailure

data class UnknownFailure(
    val message: String? = null,
) : LoginFailure
