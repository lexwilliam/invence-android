package com.lexwilliam.user.usecase

import arrow.core.Either
import com.lexwilliam.user.source.SessionManager
import com.lexwilliam.user.util.LogoutFailure
import javax.inject.Inject

class LogoutUseCase
    @Inject
    constructor(
        private val sessionManager: SessionManager
    ) {
        suspend operator fun invoke(): Either<LogoutFailure, Boolean> {
            return sessionManager.clearSession()
        }
    }