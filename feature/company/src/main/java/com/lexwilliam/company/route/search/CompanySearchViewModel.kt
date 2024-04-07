package com.lexwilliam.company.route.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexwilliam.company.model.CompanyBranch
import com.lexwilliam.company.navigation.CompanySearchNavigationTarget
import com.lexwilliam.company.usecase.FetchCompanyUseCase
import com.lexwilliam.user.usecase.FetchUserUseCase
import com.lexwilliam.user.usecase.ObserveSessionUseCase
import com.lexwilliam.user.usecase.UpsertUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanySearchViewModel
    @Inject
    constructor(
        private val fetchCompany: FetchCompanyUseCase,
        observeSession: ObserveSessionUseCase,
        private val fetchUser: FetchUserUseCase,
        private val upsertUser: UpsertUserUseCase
    ) : ViewModel() {
        private val _state = MutableStateFlow(CompanySearchUiState())
        val uiState = _state.asStateFlow()

        private val userUUID =
            observeSession()
                .map { session ->
                    session.userUUID
                }

        private val user =
            userUUID
                .map { id -> id?.let { fetchUser(it) } }

        private val _navigation = Channel<CompanySearchNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        init {
            viewModelScope.launch {
                val user =
                    observeSession()
                        .map { session ->
                            session.userUUID
                                ?.let { fetchUser(it) }
                        }
                        .firstOrNull()
                        ?.getOrNull()
                when (user?.branchUUID == null) {
                    true -> _state.update { old -> old.copy(showSearch = true) }
                    false -> _navigation.send(CompanySearchNavigationTarget.Home)
                }
            }
        }

        fun onEvent(event: CompanySearchUiEvent) {
            when (event) {
                is CompanySearchUiEvent.QueryChanged -> handleQueryChanged(event.value)
                CompanySearchUiEvent.ConfirmClicked -> handleConfirmClicked()
                CompanySearchUiEvent.CreateCompanyClicked -> handleCreateCompanyClicked()
                is CompanySearchUiEvent.BranchSelected -> handleBranchSelected(event.branch)
                CompanySearchUiEvent.DismissDialog -> handleDismissDialog()
            }
        }

        private fun handleBranchSelected(branch: CompanyBranch) {
            viewModelScope.launch {
                val user = user.firstOrNull()?.getOrNull() ?: return@launch
                upsertUser(user.copy(branchUUID = branch.uuid)).fold(
                    ifLeft = { failure ->
                        Log.d("TAG", failure.toString())
                    },
                    ifRight = {
                        _navigation.send(CompanySearchNavigationTarget.Home)
                    }
                )
            }
        }

        private fun handleDismissDialog() {
            _state.update { old -> old.copy(isDialogShown = false) }
        }

        private fun handleCreateCompanyClicked() {
            viewModelScope.launch {
                _navigation.send(CompanySearchNavigationTarget.CompanyForm)
            }
        }

        private fun handleConfirmClicked() {
            _state.update { old -> old.copy(error = null) }
            viewModelScope.launch {
                fetchCompany(_state.value.query).fold(
                    ifLeft = { failure ->
                        _state.update { old -> old.copy(error = failure.toString()) }
                    },
                    ifRight = {
                        _state.update { old ->
                            old.copy(
                                isDialogShown = true,
                                branches = it.branches
                            )
                        }
                    }
                )
            }
        }

        private fun handleQueryChanged(value: String) {
            _state.update { old -> old.copy(query = value) }
        }
    }