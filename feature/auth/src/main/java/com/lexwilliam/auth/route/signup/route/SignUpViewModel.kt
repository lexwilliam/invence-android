package com.lexwilliam.auth.route.signup.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import arrow.optics.copy
import com.lexwilliam.auth.route.signup.navigation.SignUpNavigationTarget
import com.lexwilliam.user.usecase.SignUpUseCase
import com.lexwilliam.user.util.SignUpFailure
import com.lexwilliam.user.util.UnknownFailure
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel
    @Inject
    constructor(
        private val signUp: SignUpUseCase
    ) : ViewModel() {
        private val _state = MutableStateFlow(SignUpUiState())
        val state = _state.asStateFlow()

        private val _navigation = Channel<SignUpNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        fun onEvent(event: SignUpUiEvent) {
            when (event) {
                is SignUpUiEvent.EmailChanged -> handleEmailChanged(event.email)
                is SignUpUiEvent.PasswordChanged -> handlePasswordChanged(event.password)
                is SignUpUiEvent.PasswordConfirmChanged ->
                    handleConfirmPasswordChanged(
                        event.password
                    )
                SignUpUiEvent.PasswordVisibilityChanged -> handlePasswordVisibilityChanged()
                SignUpUiEvent.SignUpClicked -> handleSignUpClicked()
                SignUpUiEvent.ConfirmPasswordVisibilityChanged ->
                    handleConfirmPasswordVisibilityChanged()
                SignUpUiEvent.BackClicked -> handleBackClicked()
                is SignUpUiEvent.NameChanged -> handleNameChanged(event.name)
            }
        }

        private fun handleNameChanged(name: String) {
            _state.update { old -> old.copy(name = name) }
        }

        private fun handleBackClicked() {
            viewModelScope.launch {
                _navigation.send(SignUpNavigationTarget.BackStack)
            }
        }

        private fun handleConfirmPasswordVisibilityChanged() {
            _state.update {
                    old ->
                old.copy(isConfirmPasswordShowing = !old.isConfirmPasswordShowing)
            }
        }

        private fun handleEmailChanged(email: String) {
            _state.update { old -> old.copy(email = email) }
        }

        private fun handlePasswordChanged(password: String) {
            _state.update { old -> old.copy(password = password) }
        }

        private fun handleConfirmPasswordChanged(password: String) {
            _state.update { old -> old.copy(confirmPassword = password) }
        }

        private fun handlePasswordVisibilityChanged() {
            _state.update { old -> old.copy(isPasswordShowing = !old.isPasswordShowing) }
        }

        private fun handleSignUpClicked() {
            viewModelScope.launch {
                when (
                    val result =
                        signUp(
                            email = _state.value.email,
                            username = _state.value.name,
                            password = _state.value.password
                        )
                ) {
                    is Either.Left ->
                        _state.update { old ->
                            old.copy(error = localizeSignUpFailure(result.value))
                        }
                    is Either.Right -> _navigation.send(SignUpNavigationTarget.Home)
                }
            }
        }

        private fun localizeSignUpFailure(failure: SignUpFailure): String =
            when (failure) {
                SignUpFailure.CreateUserTaskFail -> "Create user task fail"
                is UnknownFailure -> failure.message ?: "Unknown Failure"
                SignUpFailure.UpsertUserToFirestoreFail -> "Insert user to database fail"
            }
    }