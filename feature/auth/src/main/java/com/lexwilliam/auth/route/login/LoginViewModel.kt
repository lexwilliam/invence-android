package com.lexwilliam.auth.route.login

import androidx.lifecycle.ViewModel
import com.lexwilliam.auth.util.SignInResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor() : ViewModel() {
        private val _state = MutableStateFlow(LoginState())
        val state = _state.asStateFlow()

        fun onEvent(event: LoginUiEvent) {
            when (event) {
                is LoginUiEvent.SignIn -> handleSignIn(event.result)
            }
        }

        private fun handleSignIn(result: SignInResult) {
            _state.update { old ->
                old.copy(
                    isSuccessful = result.data != null,
                    error = result.errorMessage,
                )
            }
        }

        fun resetState() {
            _state.update { LoginState() }
        }
    }
