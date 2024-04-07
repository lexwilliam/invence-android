package com.lexwilliam.transaction.history.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexwilliam.core.extensions.toFormatString
import com.lexwilliam.log.usecase.ObserveLogUseCase
import com.lexwilliam.transaction.history.navigation.TransactionHistoryNavigationTarget
import com.lexwilliam.transaction.model.Inbox
import com.lexwilliam.transaction.usecase.ObserveTransactionUseCase
import com.lexwilliam.user.usecase.FetchUserUseCase
import com.lexwilliam.user.usecase.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TransactionHistoryViewModel
    @Inject
    constructor(
        observeTransaction: ObserveTransactionUseCase,
        observeLog: ObserveLogUseCase,
        observeSession: ObserveSessionUseCase,
        private val fetchUser: FetchUserUseCase
    ) : ViewModel() {
        private val _navigation = Channel<TransactionHistoryNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        private val user =
            observeSession().map { session ->
                session.userUUID
                    ?.let { fetchUser(it) }
                    ?.getOrNull()
            }

        private val branchUUID = user.map { it?.branchUUID }

        private val transactions =
            branchUUID.flatMapLatest { uuid ->
                when (uuid) {
                    null -> flowOf(emptyList())
                    else -> observeTransaction(uuid)
                }
            }

        private val logs =
            branchUUID.flatMapLatest { uuid ->
                when (uuid) {
                    null -> flowOf(emptyList())
                    else -> observeLog(uuid)
                }
            }

        val inbox =
            combine(
                transactions,
                logs
            ) { transactions, logs ->
                transactions
                    .map { Inbox.InboxTransaction(transaction = it) }
                    .plus(logs.map { Inbox.InboxLog(log = it) })
                    .sortedByDescending { it.createdAt.epochSeconds }
                    .groupBy { it.createdAt.toFormatString("EEE, dd MMM yyyy") }
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyMap()
            )

        fun handleBackStackClicked() {
            viewModelScope.launch {
                _navigation.send(TransactionHistoryNavigationTarget.BackStack)
            }
        }
    }