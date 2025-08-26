package com.lexwilliam.core.session

sealed interface SessionFailure {
    data object SessionNotFound : SessionFailure
}