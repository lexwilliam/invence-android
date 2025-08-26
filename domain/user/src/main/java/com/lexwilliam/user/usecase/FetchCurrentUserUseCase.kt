package com.lexwilliam.user.usecase

import arrow.core.Either
import com.lexwilliam.user.model.User
import com.lexwilliam.user.repository.UserRepository
import com.lexwilliam.user.util.FetchUserFailure
import javax.inject.Inject

class FetchCurrentUserUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository
    ) {
        suspend operator fun invoke(): Either<FetchUserFailure, User> {
            return userRepository.fetchCurrentUser()
        }
    }