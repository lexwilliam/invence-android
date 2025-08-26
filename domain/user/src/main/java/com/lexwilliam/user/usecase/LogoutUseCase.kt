package com.lexwilliam.user.usecase

import arrow.core.Either
import com.lexwilliam.core.session.SessionFailure
import com.lexwilliam.core.session.SessionManager
import javax.inject.Inject

class LogoutUseCase
    @Inject
    constructor(
        private val sessionManager: SessionManager
    ) {
        suspend operator fun invoke(): Either<SessionFailure, Boolean> {
            return sessionManager.clearSession()
        }
    }