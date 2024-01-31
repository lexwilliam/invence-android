package com.lexwilliam.auth.route.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexwilliam.auth.navigation.LoginNavigationTarget
import com.lexwilliam.auth.util.SignInResult
import com.lexwilliam.user.model.User
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
        private val upsertUser: UpsertUserUseCase
    ) : ViewModel() {
        private val _state = MutableStateFlow(LoginUiState())
        val state = _state.asStateFlow()

        private val _navigation = Channel<LoginNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        fun onEvent(event: LoginUiEvent) {
            when (event) {
                is LoginUiEvent.SignIn -> handleSignIn(event.result)
                is LoginUiEvent.Success -> handleSuccess()
                is LoginUiEvent.UserAlreadyAuthenticated -> handleUserAlreadyAuthenticated()
            }
        }

        private fun handleUserAlreadyAuthenticated() {
            viewModelScope.launch {
                _navigation.send(LoginNavigationTarget.Inventory)
            }
        }

        private fun handleSignIn(result: SignInResult) {
            _state.update { old ->
                val userData = result.data
                old.copy(
                    user =
                        User(
                            uuid = userData?.userId ?: "",
                            name = userData?.username ?: "",
                            imageUrl = userData?.profilePictureUrl,
                            createdAt = Clock.System.now()
                        ),
                    error = result.errorMessage
                )
            }
        }

        private fun handleSuccess() {
            viewModelScope.launch {
                val user = _state.value.user ?: return@launch
                upsertUser(user).fold(
                    ifLeft = { failure ->
                        Log.d("TAG", failure.toString())
                    },
                    ifRight = {
                        _navigation.send(LoginNavigationTarget.Inventory)
                        _state.update { LoginUiState() }
                    }
                )
            }
        }
    }