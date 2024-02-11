package com.lexwilliam.user.usecase

import com.lexwilliam.user.session.Session
import com.lexwilliam.user.source.SessionManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSessionUseCase
    @Inject
    constructor(
        private val sessionManager: SessionManager
    ) {
        operator fun invoke(): Flow<Session> = sessionManager.sessionDataFlow
    }