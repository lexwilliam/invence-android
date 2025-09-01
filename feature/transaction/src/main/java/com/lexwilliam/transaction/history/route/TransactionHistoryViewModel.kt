package com.lexwilliam.transaction.history.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.lexwilliam.transaction.history.navigation.TransactionHistoryNavigationTarget
import com.lexwilliam.transaction.usecase.ObservePagedTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TransactionHistoryViewModel
    @Inject
    constructor(
        observePagedTransaction: ObservePagedTransactionUseCase
    ) : ViewModel() {
        private val _navigation = Channel<TransactionHistoryNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        val transactions =
            observePagedTransaction().cachedIn(viewModelScope)

        fun onTransactionClick(transactionUUID: UUID) {
            viewModelScope.launch {
                _navigation.send(
                    TransactionHistoryNavigationTarget.TransactionDetail(transactionUUID)
                )
            }
        }
    }