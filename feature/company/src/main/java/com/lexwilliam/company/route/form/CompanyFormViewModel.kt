package com.lexwilliam.company.route.form

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.lexwilliam.branch.model.Branch
import com.lexwilliam.branch.usecase.UploadBranchImageUseCase
import com.lexwilliam.branch.usecase.UpsertBranchUseCase
import com.lexwilliam.branch.util.UpsertBranchFailure
import com.lexwilliam.company.model.Company
import com.lexwilliam.company.model.CompanyBranch
import com.lexwilliam.company.navigation.CompanyFormNavigationTarget
import com.lexwilliam.company.route.form.dialog.CompanyFormDialogEvent
import com.lexwilliam.company.route.form.dialog.CompanyFormDialogState
import com.lexwilliam.company.usecase.UpsertCompanyUseCase
import com.lexwilliam.company.util.UnknownFailure
import com.lexwilliam.company.util.UpsertCompanyFailure
import com.lexwilliam.core.model.SnackbarMessage
import com.lexwilliam.core.util.UploadImageFailure
import com.lexwilliam.user.model.Role
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
        private val uploadBranchImage: UploadBranchImageUseCase,
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
                is CompanyFormUiEvent.DismissMessage -> handleDismissMessage(event.id)
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
                            logoUrl = branch.imageUrl,
                            createdAt = Clock.System.now()
                        )
                    }
                upsertCompany(company).fold(
                    ifLeft = { failure -> handleUpsertCompanyFailure(failure) },
                    ifRight = {
                        var successfulUpsert = 0
                        branches.forEach { branch ->
                            upsertBranch(branch).fold(
                                ifLeft = { failure ->
                                    handleUpsertBranchFailure(failure)
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
                                        upsertUser(
                                            userResult.copy(
                                                branchUUID = branch.uuid,
                                                role = Role.OWNER
                                            )
                                        ).fold(
                                            ifLeft = { failure ->
                                                Log.d("TAG", failure.toString())
                                            },
                                            ifRight = {
                                                _navigation.send(
                                                    CompanyFormNavigationTarget.Home
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

        private fun handleUpsertBranchFailure(failure: UpsertBranchFailure) {
            val message =
                SnackbarMessage(
                    id = UUID.randomUUID(),
                    message = localizeUpsertBranchFailure(failure),
                    action = null,
                    actionLabel = null
                )
            _state.update { old -> old.copy(messages = old.messages + message) }
        }

        private fun localizeUpsertBranchFailure(failure: UpsertBranchFailure): String {
            return when (failure) {
                is UpsertBranchFailure.UnknownFailure -> failure.message ?: "Unknown failure"
            }
        }

        private fun handleUpsertCompanyFailure(failure: UpsertCompanyFailure) {
            val message =
                SnackbarMessage(
                    id = UUID.randomUUID(),
                    message = localizeUpsertCompanyFailure(failure),
                    action = null,
                    actionLabel = null
                )
            _state.update { old -> old.copy(messages = old.messages + message) }
        }

        private fun localizeUpsertCompanyFailure(failure: UpsertCompanyFailure): String {
            return when (failure) {
                is UnknownFailure -> failure.message ?: "Unknown failure"
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
                is CompanyFormDialogEvent.ImageChanged -> handleDialogImageChanged(event.bmp)
                CompanyFormDialogEvent.Dismiss -> handleDialogDismiss()
                CompanyFormDialogEvent.Confirm -> handleDialogConfirm()
            }
        }

        private fun handleDialogNameChanged(value: String) {
            _dialogState.update { old -> old?.copy(name = value) }
        }

        private fun handleDialogImageChanged(image: Any?) {
            _dialogState.update { old -> old?.copy(image = image) }
        }

        private fun handleDialogDismiss() {
            _dialogState.update { null }
        }

        private fun handleDialogConfirm() {
            viewModelScope.launch {
                val dialogState = _dialogState.value ?: return@launch
                val branchUUID = UUID.randomUUID()
                var imageUrl: String? = null
                if (dialogState.image != null) {
                    when (val result = uploadBranchImage(branchUUID, dialogState.image)) {
                        is Either.Left -> handleUploadImageFailure(result.value)
                        is Either.Right -> imageUrl = result.value.toString()
                    }
                }
                val branch =
                    CompanyBranch(
                        uuid = branchUUID,
                        name = "${dialogState.name} Branch",
                        imageUrl = imageUrl
                    )

                _state.update { old -> old.copy(branchList = old.branchList + branch) }
                _dialogState.update { null }
            }
        }

        private fun handleUploadImageFailure(failure: UploadImageFailure) {
            val message =
                SnackbarMessage(
                    id = UUID.randomUUID(),
                    message = localizeUploadImageFailure(failure),
                    action = null,
                    actionLabel = null
                )
            _state.update { old -> old.copy(messages = old.messages + message) }
        }

        private fun handleDismissMessage(id: UUID) {
            _state.update { old -> old.copy(messages = old.messages.filterNot { it.id == id }) }
        }

        private fun localizeUploadImageFailure(failure: UploadImageFailure): String {
            return when (failure) {
                is UploadImageFailure.UnknownFailure -> failure.message ?: "Unknown failure"
                UploadImageFailure.UploadTaskFailed -> "Upload task failed"
                UploadImageFailure.WrongFormat -> "The file you upload is the wrong format"
            }
        }
    }