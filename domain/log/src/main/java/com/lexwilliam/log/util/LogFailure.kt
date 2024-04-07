package com.lexwilliam.log.util

sealed interface UpsertLogFailure {
    data class UnknownFailure(
        val message: String?
    ) : UpsertLogFailure
}

sealed interface DeleteLogFailure {
    data class UnknownFailure(
        val message: String?
    ) : DeleteLogFailure
}