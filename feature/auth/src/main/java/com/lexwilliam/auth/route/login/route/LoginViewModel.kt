package com.lexwilliam.auth.route.login.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.lexwilliam.auth.route.login.navigation.LoginNavigationTarget
import com.lexwilliam.auth.util.SignInResult
import com.lexwilliam.core_ui.controller.SnackbarController
import com.lexwilliam.core_ui.controller.SnackbarEvent
import com.lexwilliam.core_ui.model.SnackbarTypeEnum
import com.lexwilliam.user.model.User
import com.lexwilliam.user.usecase.FetchUserUseCase
import com.lexwilliam.user.usecase.LoginUseCase
import com.lexwilliam.user.usecase.LogoutUseCase
import com.lexwilliam.user.usecase.UpsertUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val login: LoginUseCase,
        private val logout: LogoutUseCase,
        private val upsertUser: UpsertUserUseCase,
        private val fetchUser: FetchUserUseCase
    ) : ViewModel() {
        private val _state = MutableStateFlow(LoginUiState())
        val state = _state.asStateFlow()

        private val _navigation = Channel<LoginNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        fun onEvent(event: LoginUiEvent) {
            when (event) {
                is LoginUiEvent.SignInWithGoogle -> handleSignInWithGoogle(event.result)
                is LoginUiEvent.EmailChanged -> handleEmailChanged(event.value)
                is LoginUiEvent.PasswordChanged -> handlePasswordChanged(event.value)
                LoginUiEvent.SignInClicked -> handleSignInClicked()
                LoginUiEvent.PasswordVisibilityChanged -> handlePasswordVisibilityChanged()
                LoginUiEvent.ForgotPasswordClicked -> handleForgotPasswordClicked()
                LoginUiEvent.SignUpClicked -> handleSignUpClicked()
            }
        }

        private fun handleSignInClicked() {
            _state.update { old -> old.copy(isLoading = true) }
            viewModelScope.launch {
                when (login(state.value.email, state.value.password)) {
                    is Either.Left -> {
                        _state.update { old -> old.copy(isLoading = false) }
                        SnackbarController.sendEvent(
                            event =
                                SnackbarEvent(
                                    type =
                                        SnackbarTypeEnum.ERROR,
                                    message = "Incorrect email or password"
                                )
                        )
                    }
                    is Either.Right -> {
                        _state.update { old -> old.copy(isLoading = false) }
                        _navigation.send(LoginNavigationTarget.Home)
                    }
                }
            }
        }

        private fun handleForgotPasswordClicked() {
            viewModelScope.launch {
                _navigation.send(LoginNavigationTarget.ForgotPassword)
            }
        }

        private fun handleSignUpClicked() {
            viewModelScope.launch {
                _navigation.send(LoginNavigationTarget.SignUp)
            }
        }

        private fun handlePasswordVisibilityChanged() {
            _state.update { old -> old.copy(isPasswordShowing = !old.isPasswordShowing) }
        }

        private fun handlePasswordChanged(value: String) {
            _state.update { old -> old.copy(password = value) }
        }

        private fun handleEmailChanged(email: String) {
            _state.update { old -> old.copy(email = email) }
        }

        private fun handleSignInWithGoogle(result: SignInResult) {
            _state.update { old -> old.copy(isGoogleSignInLoading = true) }
            viewModelScope.launch {
                val errorMessage = result.errorMessage
                val userData = result.data
                when {
                    errorMessage != null -> {
                        _state.update { old -> old.copy(isGoogleSignInLoading = false) }
                        SnackbarController.sendEvent(
                            event =
                                SnackbarEvent(
                                    type =
                                        SnackbarTypeEnum.ERROR,
                                    message = errorMessage
                                )
                        )
                    }
                    userData == null -> {
                        _state.update { old -> old.copy(isGoogleSignInLoading = false) }
                        SnackbarController.sendEvent(
                            event =
                                SnackbarEvent(
                                    type =
                                        SnackbarTypeEnum.ERROR,
                                    message = "Can't find user data"
                                )
                        )
                    }
                    else -> {
                        val user =
                            User(
                                uuid = userData.userId,
                                name = userData.username ?: "",
                                email = userData.email ?: "",
                                imageUrl = userData.profilePictureUrl,
                                createdAt = Clock.System.now()
                            )

                        val userDoc = fetchUser(user.uuid).getOrNull()
                        if (userDoc != null) {
                            upsertNewUser(user)
                        } else {
                            _state.update { old -> old.copy(isGoogleSignInLoading = false) }
                            _navigation.send(LoginNavigationTarget.Home)
                        }
                    }
                }
            }
        }

        private suspend fun upsertNewUser(user: User) {
            when (val upsertResult = upsertUser(user)) {
                is Either.Left -> {
                    _state.update { old -> old.copy(isGoogleSignInLoading = false) }
                    SnackbarController.sendEvent(
                        event =
                            SnackbarEvent(
                                type =
                                    SnackbarTypeEnum.ERROR,
                                message = "${upsertResult.value}: Insert User Failed"
                            )
                    )
                    logout()
                }
                is Either.Right -> {
                    _state.update { old -> old.copy(isGoogleSignInLoading = false) }
                    _navigation.send(LoginNavigationTarget.Home)
                }
            }
        }
    }