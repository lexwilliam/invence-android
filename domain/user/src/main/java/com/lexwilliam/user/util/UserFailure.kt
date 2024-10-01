package com.lexwilliam.user.util

sealed interface LoginFailure {
    data object TaskFailed : LoginFailure

    data object UserNotFound : LoginFailure
}

sealed interface LogoutFailure

sealed interface SignUpFailure {
    data object CreateUserTaskFail : SignUpFailure

    data object UpsertUserToFirestoreFail : SignUpFailure
}

sealed interface FetchUserFailure {
    data object UserIsNull : FetchUserFailure

    data object DocumentNotFound : FetchUserFailure
}

sealed interface UpsertUserFailure

sealed interface UpsertShiftFailure

data class UnknownFailure(
    val message: String? = null
) : LoginFailure,
    LogoutFailure,
    UpsertUserFailure,
    FetchUserFailure,
    UpsertShiftFailure,
    SignUpFailure