package com.lexwilliam.profile.route

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.lexwilliam.profile.navigation.ProfileNavigationTarget
import com.lexwilliam.user.usecase.FetchUserUseCase
import com.lexwilliam.user.usecase.LogoutUseCase
import com.lexwilliam.user.usecase.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val fetchUser: FetchUserUseCase,
        observeSession: ObserveSessionUseCase,
        private val logout: LogoutUseCase
    ) : ViewModel() {
        private val _navigation = Channel<ProfileNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        private val _isLogoutShowing = MutableStateFlow(false)
        val isLogoutShowing = _isLogoutShowing.asStateFlow()

        val user =
            observeSession()
                .map { session ->
                    session.userUUID
                        ?.let { fetchUser(it) }
                        ?.getOrNull()
                }
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5_000),
                    null
                )

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