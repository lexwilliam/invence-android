package com.lexwilliam.company.util

sealed interface FetchCompanyFailure {
    data object CompanyIsNull : FetchCompanyFailure

    data object DocumentNotFound : FetchCompanyFailure
}

sealed interface UpsertCompanyFailure

sealed interface DeleteCompanyFailure

sealed interface InviteCompanyFailure {
    data object CompanyNotFound : InviteCompanyFailure
}

data class UnknownFailure(
    val message: String?
) : FetchCompanyFailure, UpsertCompanyFailure, DeleteCompanyFailure, InviteCompanyFailure