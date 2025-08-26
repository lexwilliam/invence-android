package com.lexwilliam.core.session

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSessionUseCase
    @Inject
    constructor(
        private val sessionManager: SessionManager
    ) {
        operator fun invoke(): Flow<Session> = sessionManager.sessionDataFlow
    }