package com.lexwilliam.auth.route.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor() : ViewModel() {
        fun onEvent(event: LoginUiEvent) {
            when (event) {
                LoginUiEvent.LoginTapped -> handleLoginTapped()
            }
        }

        private fun handleLoginTapped() {
        }
    }
