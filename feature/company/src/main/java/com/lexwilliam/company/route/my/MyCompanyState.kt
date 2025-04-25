package com.lexwilliam.company.route.my

import com.lexwilliam.company.model.Company

data class MyCompanyState(
    val company: Company? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false
)