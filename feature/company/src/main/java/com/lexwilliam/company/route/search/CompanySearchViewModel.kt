package com.lexwilliam.company.route.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexwilliam.company.navigation.CompanySearchNavigationTarget
import com.lexwilliam.company.usecase.FetchCompanyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanySearchViewModel
    @Inject
    constructor(
        private val fetchCompany: FetchCompanyUseCase
    ) : ViewModel() {
        private val _state = MutableStateFlow(CompanySearchUiState())
        val uiState = _state.asStateFlow()

        private val _navigation = Channel<CompanySearchNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        fun onEvent(event: CompanySearchUiEvent) {
            when (event) {
                is CompanySearchUiEvent.QueryChanged -> handleQueryChanged(event.value)
                CompanySearchUiEvent.ConfirmClicked -> handleConfirmClicked()
                CompanySearchUiEvent.CreateCompanyClicked -> handleCreateCompanyClicked()
            }
        }

        private fun handleCreateCompanyClicked() {
            viewModelScope.launch {
                _navigation.send(CompanySearchNavigationTarget.CompanyForm)
            }
        }

        private fun handleConfirmClicked() {
            viewModelScope.launch {
                fetchCompany(_state.value.query).fold(
                    ifLeft = { failure ->
                        Log.d("TAG", failure.toString())
                    },
                    ifRight = {
                        TODO("Send an invite request to the company")
                    }
                )
            }
        }

        private fun handleQueryChanged(value: String) {
            _state.update { old -> old.copy(query = value) }
        }
    }