package com.lexwilliam.company.route.form

import com.lexwilliam.company.model.CompanyBranch
import com.lexwilliam.core.model.SnackbarMessage
import com.lexwilliam.core.util.generateCompanyId

data class CompanyFormUiState(
    val step: Int = 1,
    val companyName: String = "",
    val branchList: List<CompanyBranch> = emptyList(),
    val selectedBranch: CompanyBranch? = null,
    val messages: List<SnackbarMessage> = emptyList()
) {
    val companyId = generateCompanyId(companyName)
}