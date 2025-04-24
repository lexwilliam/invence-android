package com.lexwilliam.company.route.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexwilliam.company.repository.CompanyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MyCompanyViewModel
    @Inject
    constructor(
        private val companyRepository: CompanyRepository
    ) : ViewModel() {
        val company = companyRepository.observeCurrentCompany()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                null
            )
    }