package com.lexwilliam.invence.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexwilliam.user.usecase.FetchUserUseCase
import com.lexwilliam.user.usecase.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppViewModel
    @Inject
    constructor(
        observeSession: ObserveSessionUseCase,
        fetchUser: FetchUserUseCase
    ) : ViewModel() {
        val branchUUID =
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

        val userUUID =
            observeSession()
                .map { session ->
                    session.userUUID
                }.stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5_000),
                    null
                )
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
    }