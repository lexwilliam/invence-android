package com.lexwilliam.company.route.form

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexwilliam.branch.model.Branch
import com.lexwilliam.branch.usecase.UpsertBranchUseCase
import com.lexwilliam.company.model.Company
import com.lexwilliam.company.model.CompanyBranch
import com.lexwilliam.company.navigation.CompanyFormNavigationTarget
import com.lexwilliam.company.route.form.dialog.CompanyFormDialogEvent
import com.lexwilliam.company.route.form.dialog.CompanyFormDialogState
import com.lexwilliam.company.usecase.UpsertCompanyUseCase
import com.lexwilliam.core.model.UploadImageFormat
import com.lexwilliam.user.usecase.FetchUserUseCase
import com.lexwilliam.user.usecase.ObserveSessionUseCase
import com.lexwilliam.user.usecase.UpsertUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CompanyFormViewModel
    @Inject
    constructor(
        private val upsertCompany: UpsertCompanyUseCase,
        private val upsertBranch: UpsertBranchUseCase,
        private val observeSession: ObserveSessionUseCase,
        private val fetchUser: FetchUserUseCase,
        private val upsertUser: UpsertUserUseCase
    ) : ViewModel() {
        private val _state = MutableStateFlow(CompanyFormUiState())
        val uiState = _state.asStateFlow()

        private val _navigation = Channel<CompanyFormNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        fun onEvent(event: CompanyFormUiEvent) {
            when (event) {
                is CompanyFormUiEvent.NameChanged -> handleNameChanged(event.value)
                CompanyFormUiEvent.AddBranch -> handleAddBranch()
                is CompanyFormUiEvent.StepChanged -> handleStepChanged(event.step)
                is CompanyFormUiEvent.BranchSelected -> handleBranchSelected(event.branch)
            }
        }

        private fun handleBranchSelected(branch: CompanyBranch) {
            _state.update { old -> old.copy(selectedBranch = branch) }
        }

        private fun onDone() {
            viewModelScope.launch {
                val company =
                    Company(
                        uuid = _state.value.companyId,
                        name = _state.value.companyName,
                        logoUrl = null,
                        branches = _state.value.branchList,
                        createdAt = Clock.System.now()
                    )
                val branches =
                    _state.value.branchList.map { branch ->
                        Branch(
                            uuid = branch.uuid,
                            name = branch.name,
                            logoUrl = null,
                            createdAt = Clock.System.now()
                        )
                    }
                upsertCompany(company).fold(
                    ifLeft = { failure ->
                        Log.d("TAG", failure.toString())
                    },
                    ifRight = {
                        var successfulUpsert = 0
                        branches.forEach { branch ->
                            upsertBranch(branch).fold(
                                ifLeft = { failure ->
                                    Log.d("TAG", failure.toString())
                                },
                                ifRight = {
                                    successfulUpsert++
                                }
                            )
                        }
                        if (successfulUpsert == branches.size) {
                            val user =
                                observeSession().firstOrNull()
                                    ?.userUUID
                                    ?.let { fetchUser(userID = it) }
                            user?.fold(
                                ifLeft = { failure ->
                                    Log.d("TAG", failure.toString())
                                },
                                ifRight = { userResult ->
                                    _state.value.selectedBranch?.let { branch ->
                                        upsertUser(userResult.copy(branchUUID = branch.uuid)).fold(
                                            ifLeft = { failure ->
                                                Log.d("TAG", failure.toString())
                                            },
                                            ifRight = {
                                                _navigation.send(
                                                    CompanyFormNavigationTarget.Inventory
                                                )
                                            }
                                        )
                                    }
                                }
                            )
                        } else {
                            Log.d("TAG", "Some of the branch failed to insert")
                        }
                    }
                )
            }
        }

        private fun handleStepChanged(step: Int) {
            when (step) {
                // Next of last page
                3 -> onDone()
                else -> _state.update { old -> old.copy(step = step) }
            }
        }

        private fun handleNameChanged(value: String) {
            _state.update { old -> old.copy(companyName = value) }
        }

        private fun handleAddBranch() {
            _dialogState.update { CompanyFormDialogState() }
        }

        private val _dialogState = MutableStateFlow<CompanyFormDialogState?>(null)
        val dialogState = _dialogState.asStateFlow()

        fun onDialogEvent(event: CompanyFormDialogEvent) {
            when (event) {
                is CompanyFormDialogEvent.NameChanged -> handleDialogNameChanged(event.value)
                is CompanyFormDialogEvent.ImageChanged -> handleDialogImageChanged(event.format)
                CompanyFormDialogEvent.Dismiss -> handleDialogDismiss()
                CompanyFormDialogEvent.Confirm -> handleDialogConfirm()
            }
        }

        private fun handleDialogNameChanged(value: String) {
            _dialogState.update { old -> old?.copy(name = value) }
        }

        private fun handleDialogImageChanged(format: UploadImageFormat?) {
            _dialogState.update { old -> old?.copy(imageUrl = format) }
        }

        private fun handleDialogDismiss() {
            _dialogState.update { null }
        }

        private fun handleDialogConfirm() {
            viewModelScope.launch {
                val dialogState = _dialogState.value ?: return@launch
                val branch =
                    CompanyBranch(
                        uuid = UUID.randomUUID(),
                        name = "${dialogState.name} Branch"
                    )
                _state.update { old -> old.copy(branchList = old.branchList + branch) }
                _dialogState.update { null }
            }
        }
    }