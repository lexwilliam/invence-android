package com.lexwilliam.product.util

sealed interface FetchProductFailure

sealed interface UpsertProductFailure

sealed interface DeleteProductFailure

sealed interface UploadImageFailure {
    data class UploadFailure(val message: String?) : UploadImageFailure
}

data class UnknownFailure(
    val message: String?
) : FetchProductFailure, UpsertProductFailure, DeleteProductFailure, UploadImageFailure