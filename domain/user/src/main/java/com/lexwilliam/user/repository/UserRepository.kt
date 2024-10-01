package com.lexwilliam.user.repository

import arrow.core.Either
import com.lexwilliam.user.model.EmployeeShift
import com.lexwilliam.user.model.User
import com.lexwilliam.user.util.FetchUserFailure
import com.lexwilliam.user.util.LoginFailure
import com.lexwilliam.user.util.SignUpFailure
import com.lexwilliam.user.util.UpsertShiftFailure
import com.lexwilliam.user.util.UpsertUserFailure
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun login(
        email: String,
        password: String
    ): Either<LoginFailure, User>

    suspend fun signUp(
        email: String,
        username: String,
        password: String
    ): Either<SignUpFailure, User>

    fun observeUser(uuid: String): Flow<User?>

    suspend fun fetchUser(uuid: String): Either<FetchUserFailure, User>

    suspend fun upsertUser(user: User): Either<UpsertUserFailure, User>

    fun observeShift(uuid: String): Flow<EmployeeShift?>

    suspend fun upsertShift(shift: EmployeeShift): Either<UpsertShiftFailure, EmployeeShift>
}