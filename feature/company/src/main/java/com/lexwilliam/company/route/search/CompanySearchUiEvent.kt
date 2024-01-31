package com.lexwilliam.company.route.search

sealed interface CompanySearchUiEvent {
    data class QueryChanged(val value: String) : CompanySearchUiEvent

    data object ConfirmClicked : CompanySearchUiEvent

    data object CreateCompanyClicked : CompanySearchUiEvent
}