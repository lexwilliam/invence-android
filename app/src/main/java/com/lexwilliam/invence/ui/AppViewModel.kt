package com.lexwilliam.invence.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexwilliam.core.session.ObserveSessionUseCase
import com.lexwilliam.core.session.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppViewModel
    @Inject
    constructor(
        observeSession: ObserveSessionUseCase
    ) : ViewModel() {
        val visiblePermissionsDialogQueue = mutableStateListOf<String>()

        fun dismissDialog() {
            visiblePermissionsDialogQueue.removeAt(visiblePermissionsDialogQueue.lastIndex)
        }

        fun onPermissionResult(
            permission: String,
            isGranted: Boolean
        ) {
            if (!isGranted) {
                visiblePermissionsDialogQueue.add(0, permission)
            }
        }

        val isLoggedIn: StateFlow<Session?> =
            observeSession()
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    null
                )
    }