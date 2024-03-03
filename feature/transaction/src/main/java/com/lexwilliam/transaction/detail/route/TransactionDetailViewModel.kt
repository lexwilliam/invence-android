package com.lexwilliam.transaction.detail.route

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexwilliam.transaction.detail.navigation.TransactionDetailNavigationTarget
import com.lexwilliam.transaction.usecase.ObserveSingleTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TransactionDetailViewModel
    @Inject
    constructor(
        observeSingleTransaction: ObserveSingleTransactionUseCase,
        savedStateHandle: SavedStateHandle
    ) : ViewModel() {
        private val _navigation = Channel<TransactionDetailNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        private val transactionUUID =
            savedStateHandle
                .getStateFlow<String?>("transactionUUID", null)
                .map { uuid -> uuid?.let { UUID.fromString(it) } }

        val transaction =
            transactionUUID.flatMapLatest { uuid ->
                Log.d("TAG", uuid.toString())
                when (uuid) {
                    null -> flowOf(null)
                    else -> observeSingleTransaction(uuid)
                }
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                null
            )

        fun handleBackStackClicked() {
            viewModelScope.launch {
                _navigation.send(TransactionDetailNavigationTarget.BackStackClicked)
            }
        }
    }