package com.lexwilliam.core.util

sealed interface Failure

sealed interface NetworkFailure : Failure {
    object NotConnected : NetworkFailure

    data class UnknownFailure(val message: String) : NetworkFailure

    data class RequestFailure(val code: Int) : NetworkFailure

    data class RedirectFailure(val code: Int) : NetworkFailure

    data class ServerResponseFailure(val code: Int) : NetworkFailure

    object SessionExpired : NetworkFailure
}

sealed interface LocalFailure : Failure {
    data class FailedToUpsert(val message: String? = null) : LocalFailure

    data class FailedToDelete(val message: String? = null) : LocalFailure

    data class FailedToFindDatabase(val message: String? = null) : LocalFailure

    data class FailedToFind(val message: String? = null) : LocalFailure
}