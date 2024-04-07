package com.lexwilliam.company.route.search

import com.lexwilliam.company.model.CompanyBranch

data class CompanySearchUiState(
    val query: String = "",
    val branches: List<CompanyBranch> = emptyList(),
    val error: String? = null,
    val isDialogShown: Boolean = false,
    val showSearch: Boolean = false
)