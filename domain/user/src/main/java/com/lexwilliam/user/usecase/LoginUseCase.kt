package com.lexwilliam.user.usecase

import arrow.core.Either
import com.lexwilliam.user.model.User
import com.lexwilliam.user.repository.UserRepository
import com.lexwilliam.user.util.LoginFailure
import javax.inject.Inject

class LoginUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository
    ) {
        suspend operator fun invoke(
            email: String,
            password: String
        ): Either<LoginFailure, User> {
            return userRepository.login(email, password)
        }
    }