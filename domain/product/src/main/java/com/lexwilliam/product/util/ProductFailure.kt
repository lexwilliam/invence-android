package com.lexwilliam.product.util

sealed interface FetchProductFailure

sealed interface UpsertProductFailure {
    data object UploadImageFailed : UpsertProductFailure
}

sealed interface DeleteProductFailure

data class UnknownFailure(
    val message: String?
) : FetchProductFailure, UpsertProductFailure, DeleteProductFailure