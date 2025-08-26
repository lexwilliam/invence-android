package com.lexwilliam.transaction.history.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexwilliam.transaction.history.navigation.TransactionHistoryNavigationTarget
import com.lexwilliam.transaction.usecase.ObserveTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TransactionHistoryViewModel
    @Inject
    constructor(
        observeTransaction: ObserveTransactionUseCase
    ) : ViewModel() {
        private val _navigation = Channel<TransactionHistoryNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        val transactions =
            observeTransaction()
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5_000),
                    null
                )

        fun handleBackStackClicked() {
            viewModelScope.launch {
                _navigation.send(TransactionHistoryNavigationTarget.BackStack)
            }
        }
    }