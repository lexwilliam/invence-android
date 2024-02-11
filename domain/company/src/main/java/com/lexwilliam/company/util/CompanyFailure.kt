package com.lexwilliam.company.util

sealed interface FetchCompanyFailure {
    data object CompanyIsNull : FetchCompanyFailure

    data object DocumentNotFound : FetchCompanyFailure
}

sealed interface UpsertCompanyFailure

sealed interface DeleteCompanyFailure

data class UnknownFailure(
    val message: String?
) : FetchCompanyFailure, UpsertCompanyFailure, DeleteCompanyFailure