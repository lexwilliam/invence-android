package com.lexwilliam.product.util

sealed interface FetchProductFailure

sealed interface UpsertProductFailure

sealed interface DeleteProductFailure

data class UnknownFailure(
    val message: String?
) : FetchProductFailure, UpsertProductFailure, DeleteProductFailure