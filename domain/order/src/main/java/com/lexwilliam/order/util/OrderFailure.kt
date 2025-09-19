package com.lexwilliam.order.util

sealed interface FetchGroupFailure

sealed interface UpsertGroupFailure

sealed interface DeleteGroupFailure

object NoBranch : FetchGroupFailure, UpsertGroupFailure, DeleteGroupFailure

data class UnknownFailure(
    val message: String?
) : FetchGroupFailure, UpsertGroupFailure, DeleteGroupFailure

sealed interface CheckoutFailure {
    data class UnknownFailure(val message: String?) : CheckoutFailure
}