package com.lexwilliam.company.route.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.lexwilliam.company.repository.CompanyRepository
import com.lexwilliam.company.util.FetchCompanyFailure
import com.lexwilliam.company.util.UnknownFailure
import com.lexwilliam.core_ui.controller.SnackbarController
import com.lexwilliam.core_ui.controller.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyCompanyViewModel
    @Inject
    constructor(
        private val companyRepository: CompanyRepository
    ) : ViewModel() {
        private val _state = MutableStateFlow(MyCompanyState())
        val state = _state.asStateFlow()

        init {
            initialize()
        }

        private fun initialize() {
            viewModelScope.launch {
                val result = companyRepository.fetchCurrentCompany()
                when (result) {
                    is Either.Left -> {
                        handleFetchCompanyError(result.value)
                    }
                    is Either.Right -> {
                        _state.update {
                            it.copy(company = result.value)
                        }
                    }
                }
            }
        }

    private fun handleFetchCompanyError(error: FetchCompanyFailure) {
        when (error) {
            FetchCompanyFailure.CompanyIsNull -> TODO()
            FetchCompanyFailure.DocumentNotFound -> TODO()
            is UnknownFailure -> TODO()
        }
    }
    }