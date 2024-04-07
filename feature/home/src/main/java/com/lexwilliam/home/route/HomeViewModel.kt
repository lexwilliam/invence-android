package com.lexwilliam.home.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexwilliam.core.extensions.toFormatString
import com.lexwilliam.home.model.Inbox
import com.lexwilliam.home.navigation.HomeNavigationTarget
import com.lexwilliam.log.usecase.ObserveLogUseCase
import com.lexwilliam.transaction.model.Transaction
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
class HomeViewModel
    @Inject
    constructor(
        private val fetchUser: FetchUserUseCase,
        observeSession: ObserveSessionUseCase,
        observeTransaction: ObserveTransactionUseCase,
        observeLog: ObserveLogUseCase
    ) : ViewModel() {
        private val _navigation = Channel<HomeNavigationTarget>()
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
                    else -> observeTransaction(uuid, 10)
                }
            }

        private val logs =
            branchUUID.flatMapLatest { uuid ->
                when (uuid) {
                    null -> flowOf(emptyList())
                    else -> observeLog(uuid, 10)
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

        fun onHomeIconClicked(label: String) {
            viewModelScope.launch {
                when (label) {
                    "Inventory" -> _navigation.send(HomeNavigationTarget.Inventory)
                    "Order" -> _navigation.send(HomeNavigationTarget.Cart)
                    else -> {}
                }
            }
        }

        fun seeAllClicked() {
            viewModelScope.launch {
                _navigation.send(HomeNavigationTarget.TransactionHistory)
            }
        }

        fun transactionClicked(transaction: Transaction) {
            viewModelScope.launch {
                _navigation.send(HomeNavigationTarget.TransactionDetail(transaction.uuid))
            }
        }

        fun historyClicked() {
            viewModelScope.launch {
                _navigation.send(HomeNavigationTarget.TransactionHistory)
            }
        }
    }