package com.lexwilliam.invence.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexwilliam.user.usecase.FetchUserUseCase
import com.lexwilliam.user.usecase.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AppViewModel
    @Inject
    constructor(
        observeSession: ObserveSessionUseCase,
        fetchUser: FetchUserUseCase
    ) : ViewModel() {
        val isLoggedIn =
            observeSession().flatMapLatest { session ->
                flowOf(session.userUUID != null)
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                false
            )

        val hasBranchUUID =
            observeSession().flatMapLatest { session ->
                flowOf(
                    session.userUUID
                        ?.let { fetchUser(it).getOrNull() }
                        ?.branchUUID != null
                )
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                false
            )
    }