package com.lexwilliam.invence.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.user.usecase.FetchUserUseCase
import com.lexwilliam.user.usecase.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel
    @Inject
    constructor(
        observeSession: ObserveSessionUseCase,
        fetchUser: FetchUserUseCase
    ) : ViewModel() {
        private val branchUUID =
            observeSession().map { session ->
                session.userUUID
                    ?.let {
                        fetchUser(it)
                    }
                    ?.getOrNull()
                    ?.branchUUID
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                null
            )

        private val userUUID =
            observeSession()
                .map { session ->
                    session.userUUID
                }.stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5_000),
                    null
                )

        private val _destination = MutableStateFlow(Screen.LOGIN)
        val destination = _destination.asStateFlow()

        private val _isLoading = MutableStateFlow(true)
        val isLoading = _isLoading.asStateFlow()

        val isLoggedIn =
            combine(
                branchUUID,
                userUUID
            ) { branchUUID, userUUID ->
                when {
                    userUUID == null -> AuthState.NO_ACCOUNT
                    branchUUID == null -> AuthState.NO_BRANCH
                    else -> AuthState.LOGGED_IN
                }
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                AuthState.NONE
            )

        init {
            viewModelScope.launch {
                val user =
                    observeSession()
                        .map { session ->
                            session.userUUID
                                ?.let { fetchUser(it) }
                                ?.getOrNull()
                        }
                        .firstOrNull()

                Log.d("TAG", "user: $user")

                when {
                    user == null -> _destination.update { Screen.LOGIN }
                    user.branchUUID == null -> _destination.update { Screen.COMPANY_SEARCH }
                    else -> _destination.update { Screen.INVENTORY }
                }
                _isLoading.update { false }
            }
        }
    }