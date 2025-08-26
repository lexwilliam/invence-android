package com.lexwilliam.core.session

import arrow.core.Either
import kotlinx.coroutines.flow.Flow

interface SessionManager {
    val sessionDataFlow: Flow<Session>

    suspend fun saveSession(uuid: String): Either<SessionFailure, Boolean>

    suspend fun clearSession(): Either<SessionFailure, Boolean>
}