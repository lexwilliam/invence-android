package com.lexwilliam.profile.route

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import arrow.core.computations.result
import com.lexwilliam.core_ui.controller.SnackbarController
import com.lexwilliam.core_ui.controller.SnackbarEvent
import com.lexwilliam.profile.navigation.ProfileNavigationTarget
import com.lexwilliam.user.model.User
import com.lexwilliam.user.usecase.FetchCurrentUserUseCase
import com.lexwilliam.user.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val fetchCurrentUser: FetchCurrentUserUseCase,
        private val logout: LogoutUseCase
    ) : ViewModel() {
        private val _navigation = Channel<ProfileNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        private val _isLogoutShowing = MutableStateFlow(false)
        val isLogoutShowing = _isLogoutShowing.asStateFlow()

        private val _user = MutableStateFlow<User?>(null)
        val user = _user.asStateFlow()

        init {
            viewModelScope.launch {
                when (val result = fetchCurrentUser()) {
                    is Either.Left -> {
                        SnackbarController.sendEvent(
                            event =
                                SnackbarEvent(
                                    message = result.toString()
                                )
                        )
                    }
                    is Either.Right -> {
                        _user.update { result.value }
                    }
                }
            }
        }

        fun onLogoutClicked() {
            _isLogoutShowing.value = true
        }

        fun onLogoutDismissed() {
            _isLogoutShowing.value = false
        }

        fun onLogoutConfirmed() {
            viewModelScope.launch {
                when (val result = logout()) {
                    is Either.Left -> Log.d("TAG", "$result: Logout Error")
                    is Either.Right -> _navigation.send(ProfileNavigationTarget.Login)
                }
            }
        }
    }