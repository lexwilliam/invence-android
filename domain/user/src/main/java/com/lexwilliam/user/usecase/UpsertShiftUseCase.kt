package com.lexwilliam.user.usecase

import arrow.core.Either
import com.lexwilliam.user.model.EmployeeShift
import com.lexwilliam.user.repository.UserRepository
import com.lexwilliam.user.util.UpsertShiftFailure
import javax.inject.Inject

class UpsertShiftUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository
    ) {
        suspend operator fun invoke(
            shift: EmployeeShift
        ): Either<UpsertShiftFailure, EmployeeShift> {
            return userRepository.upsertShift(shift)
        }
    }