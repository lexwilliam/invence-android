package com.lexwilliam.company.route.form

sealed interface CompanyFormUiEvent {
    data class NameChanged(val value: String): CompanyFormUiEvent

    data object AddBranch: CompanyFormUiEvent

    data class StepChanged(val step: Int): CompanyFormUiEvent
}