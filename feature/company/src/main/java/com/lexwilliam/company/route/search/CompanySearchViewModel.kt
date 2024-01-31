package com.lexwilliam.company.route.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexwilliam.company.navigation.CompanySearchNavigationTarget
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
    constructor() : ViewModel() {
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
            TODO("Not yet implemented")
        }

        private fun handleQueryChanged(value: String) {
            _state.update { old -> old.copy(query = value) }
        }
    }