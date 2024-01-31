package com.lexwilliam.company.util

sealed interface FetchCompanyFailure

sealed interface UpsertCompanyFailure

sealed interface DeleteCompanyFailure

data class UnknownFailure(
    val message: String?
) : FetchCompanyFailure, UpsertCompanyFailure, DeleteCompanyFailure