package com.lexwilliam.branch.util

sealed interface FetchBranchFailure {
    data class UnknownFailure(
        val message: String?
    ) : FetchBranchFailure
}

sealed interface UpsertBranchFailure {
    data class UnknownFailure(
        val message: String?
    ) : UpsertBranchFailure
}

sealed interface DeleteBranchFailure {
    data class UnknownFailure(
        val message: String?
    ) : DeleteBranchFailure
}