package com.lexwilliam.user.usecase

import arrow.core.Either
import com.lexwilliam.user.model.User
import com.lexwilliam.user.repository.UserRepository
import com.lexwilliam.user.util.FetchUserFailure
import javax.inject.Inject

class FetchUserUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository
    ) {
        suspend operator fun invoke(userID: String): Either<FetchUserFailure, User> {
            return userRepository.fetchUser(userID)
        }
    }