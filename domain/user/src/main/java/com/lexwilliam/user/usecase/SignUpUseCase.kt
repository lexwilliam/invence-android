package com.lexwilliam.user.usecase

import arrow.core.Either
import com.lexwilliam.user.model.User
import com.lexwilliam.user.repository.UserRepository
import com.lexwilliam.user.util.SignUpFailure
import javax.inject.Inject

class SignUpUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository
    ) {
        suspend operator fun invoke(
            email: String,
            username: String,
            password: String
        ): Either<SignUpFailure, User> {
            return userRepository.signUp(email, username, password)
        }
    }