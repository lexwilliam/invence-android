package com.lexwilliam.user.usecase

import arrow.core.Either
import com.lexwilliam.user.model.User
import com.lexwilliam.user.repository.UserRepository
import com.lexwilliam.user.util.UpsertUserFailure
import javax.inject.Inject

class UpsertUserUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository
    ) {
        suspend operator fun invoke(user: User): Either<UpsertUserFailure, User> {
            return userRepository.upsertUser(user)
        }
    }