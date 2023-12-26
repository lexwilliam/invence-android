package com.lexwilliam.user.repository

import arrow.core.Either
import com.lexwilliam.user.model.User
import com.lexwilliam.user.util.LoginFailure

interface UserRepository {
    suspend fun signInWithGoogle(): Either<LoginFailure, User>
}
