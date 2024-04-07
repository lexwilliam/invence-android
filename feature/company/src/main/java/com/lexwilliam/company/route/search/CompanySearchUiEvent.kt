package com.lexwilliam.company.route.search

import com.lexwilliam.company.model.CompanyBranch

sealed interface CompanySearchUiEvent {
    data class QueryChanged(val value: String) : CompanySearchUiEvent

    data object ConfirmClicked : CompanySearchUiEvent

    data object CreateCompanyClicked : CompanySearchUiEvent

    data class BranchSelected(val branch: CompanyBranch) : CompanySearchUiEvent

    data object DismissDialog : CompanySearchUiEvent
}