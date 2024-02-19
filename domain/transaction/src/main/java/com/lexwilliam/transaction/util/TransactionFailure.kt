package com.lexwilliam.transaction.util

sealed interface UpsertTransactionFailure

sealed interface DeleteTransactionFailure

data class UnknownFailure(
    val message: String?
) : UpsertTransactionFailure, DeleteTransactionFailure