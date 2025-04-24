package com.lexwilliam.company.route.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexwilliam.company.model.CompanyBranch
import com.lexwilliam.company.model.CompanyInviteRequest
import com.lexwilliam.company.navigation.CompanySearchNavigationTarget
import com.lexwilliam.company.usecase.FetchCompanyUseCase
import com.lexwilliam.company.usecase.SendInviteCompanyUseCase
import com.lexwilliam.company.usecase.UpsertCompanyUseCase
import com.lexwilliam.core_ui.controller.SnackbarController
import com.lexwilliam.core_ui.controller.SnackbarEvent
import com.lexwilliam.core_ui.model.SnackbarTypeEnum
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
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class CompanySearchViewModel
    @Inject
    constructor(
        private val fetchCompany: FetchCompanyUseCase,
        private val upsertCompany: UpsertCompanyUseCase,
        observeSession: ObserveSessionUseCase,
        private val fetchUser: FetchUserUseCase,
        private val upsertUser: UpsertUserUseCase,
        private val sendInvite: SendInviteCompanyUseCase
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
                        SnackbarController.sendEvent(
                            SnackbarEvent(
                                type = SnackbarTypeEnum.ERROR,
                                message = failure.toString()
                            )
                        )
                    },
                    ifRight = {
                        _navigation.send(CompanySearchNavigationTarget.Home)
                        SnackbarController.sendEvent(
                            SnackbarEvent(
                                type = SnackbarTypeEnum.SUCCESS,
                                message = ""
                            )
                        )
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
            _state.update { old -> old.copy(error = null, isLoadingSearch = true) }
            viewModelScope.launch {
                fetchCompany(_state.value.query).fold(
                    ifLeft = { failure ->
                        _state.update {
                                old ->
                            old.copy(error = failure.toString(), isLoadingSearch = false)
                        }
                        SnackbarController.sendEvent(
                            SnackbarEvent(
                                type = SnackbarTypeEnum.ERROR,
                                message = "Invite Request Failed"
                            )
                        )
                    },
                    ifRight = { company ->
                        val user = user.firstOrNull()?.getOrNull() ?: return@launch
                        val inviteRequest =
                            CompanyInviteRequest(
                                userId = user.uuid,
                                email = user.email,
                                imageUrl = user.imageUrl,
                                createdAt = Clock.System.now()
                            )
                        upsertCompany(
                            company.copy(
                                inviteRequest = company.inviteRequest + inviteRequest
                            )
                        ).fold(
                            ifLeft = { failure ->
                                SnackbarController.sendEvent(
                                    SnackbarEvent(
                                        type = SnackbarTypeEnum.ERROR,
                                        message = "Invite Request Failed"
                                    )
                                )
                            },
                            ifRight = {
                                _state.update { old ->
                                    old.copy(
                                        isLoadingSearch = false
                                    )
                                }
                                SnackbarController.sendEvent(
                                    SnackbarEvent(
                                        type = SnackbarTypeEnum.SUCCESS,
                                        message = "Invite sent successfully"
                                    )
                                )
                            }
                        )
                    }
                )
            }
        }

        private fun handleQueryChanged(value: String) {
            _state.update { old -> old.copy(query = value) }
        }
    }