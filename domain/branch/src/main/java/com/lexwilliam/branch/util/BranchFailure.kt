package com.lexwilliam.branch.util

sealed interface FetchBranchFailure

sealed interface UpsertBranchFailure

sealed interface DeleteBranchFailure

data class UnknownFailure(
    val message: String?
) : FetchBranchFailure, UpsertBranchFailure, DeleteBranchFailure