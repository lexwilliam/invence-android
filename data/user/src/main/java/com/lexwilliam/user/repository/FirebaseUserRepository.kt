package com.lexwilliam.user.repository

import arrow.core.Either
import com.lexwilliam.user.model.User
import com.lexwilliam.user.util.LoginFailure

fun firebaseUserRepository() =
    object : UserRepository {
        override suspend fun signInWithGoogle(): Either<LoginFailure, User> {
            TODO("Implement sign in with google")
        }
    }
