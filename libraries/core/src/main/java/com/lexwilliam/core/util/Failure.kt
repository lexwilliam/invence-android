package com.lexwilliam.core.util

sealed interface Failure

sealed interface UploadImageFailure {
    data object Unauthenticated : UploadImageFailure

    data object UploadTaskFailed : UploadImageFailure

    data object WrongFormat : UploadImageFailure

    data class UnknownFailure(val message: String?) : UploadImageFailure
}