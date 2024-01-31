package com.lexwilliam.company.route.form

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.lexwilliam.company.route.form.dialog.CompanyFormDialogEvent
import com.lexwilliam.company.route.form.dialog.CompanyFormDialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CompanyFormViewModel
    @Inject
    constructor() : ViewModel() {
        private val _state = MutableStateFlow(CompanyFormUiState())
        val uiState = _state.asStateFlow()

        fun onEvent(event: CompanyFormUiEvent) {
            when (event) {
                is CompanyFormUiEvent.NameChanged -> handleNameChanged(event.value)
                CompanyFormUiEvent.AddBranch -> handleAddBranch()
                is CompanyFormUiEvent.StepChanged -> handleStepChanged(event.step)
            }
        }

        private fun handleStepChanged(step: Int) {
            _state.update { old -> old.copy(step = step) }
        }

        private fun handleNameChanged(value: String) {
            _state.update { old -> old.copy(companyName = value) }
        }

        private fun handleAddBranch() {
            TODO("Not yet implemented")
        }

        private val _dialogState = MutableStateFlow<CompanyFormDialogState?>(null)
        val dialogState = _dialogState.asStateFlow()

        fun onDialogEvent(event: CompanyFormDialogEvent) {
            when (event) {
                is CompanyFormDialogEvent.NameChanged -> handleDialogNameChanged(event.value)
                is CompanyFormDialogEvent.ImageChanged -> handleDialogImageChanged(event.uri)
                CompanyFormDialogEvent.Dismiss -> handleDialogDismiss()
                CompanyFormDialogEvent.Confirm -> handleDialogConfirm()
            }
        }



        private fun handleDialogNameChanged(value: String) {
            _dialogState.update { old -> old?.copy(name = value) }
        }

        private fun handleDialogImageChanged(uri: Uri?) {
            _dialogState.update { old -> old?.copy(imageUrl = uri) }
        }

        private fun handleDialogDismiss() {
            _dialogState.update { null }
        }

        private fun handleDialogConfirm() {
            TODO("Not yet implemented")
        }
}