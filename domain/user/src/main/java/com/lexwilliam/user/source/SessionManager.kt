package com.lexwilliam.user.source

import arrow.core.Either
import com.lexwilliam.user.session.Session
import com.lexwilliam.user.util.LoginFailure
import com.lexwilliam.user.util.LogoutFailure
import kotlinx.coroutines.flow.Flow

interface SessionManager {
    val sessionDataFlow: Flow<Session>

    suspend fun saveSession(
        uuid: String,
        branchUUID: String
    ): Either<LoginFailure, Boolean>

    suspend fun clearSession(): Either<LogoutFailure, Boolean>
}