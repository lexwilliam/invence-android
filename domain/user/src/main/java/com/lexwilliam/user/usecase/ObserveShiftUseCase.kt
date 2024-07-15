package com.lexwilliam.user.usecase

import com.lexwilliam.user.model.EmployeeShift
import com.lexwilliam.user.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveShiftUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository
    ) {
        operator fun invoke(uuid: String): Flow<EmployeeShift?> {
            return userRepository.observeShift(uuid)
        }
    }