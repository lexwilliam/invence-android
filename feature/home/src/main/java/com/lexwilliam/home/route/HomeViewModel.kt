package com.lexwilliam.home.route

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.lexwilliam.core.extensions.toFormatString
import com.lexwilliam.home.model.Inbox
import com.lexwilliam.home.navigation.HomeNavigationTarget
import com.lexwilliam.log.usecase.ObserveLogUseCase
import com.lexwilliam.transaction.model.Transaction
import com.lexwilliam.transaction.usecase.ObserveTransactionUseCase
import com.lexwilliam.user.model.EmployeeShift
import com.lexwilliam.user.usecase.FetchUserUseCase
import com.lexwilliam.user.usecase.ObserveSessionUseCase
import com.lexwilliam.user.usecase.ObserveShiftUseCase
import com.lexwilliam.user.usecase.UpsertShiftUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
        observeLog: ObserveLogUseCase,
        observeShift: ObserveShiftUseCase,
        private val upsertShift: UpsertShiftUseCase
    ) : ViewModel() {
        private val _navigation = Channel<HomeNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

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

        private val branchUUID = user.map { it?.branchUUID }

        val shift =
            user.flatMapLatest { user ->
                when (user) {
                    null -> flowOf(EmployeeShift())
                    else -> observeShift(user.uuid)
                }
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                EmployeeShift()
            )

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

        private val _state = MutableStateFlow(HomeUiState())
        val uiState = _state.asStateFlow()

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

        fun previousDateClicked() {
            _state.update { old -> old.copy(date = old.date.minusMonths(1)) }
        }

        fun nextDateClicked() {
            _state.update { old -> old.copy(date = old.date.plusMonths(1)) }
        }

        fun checkInClicked() {
            viewModelScope.launch {
                val user = user.firstOrNull() ?: return@launch
                val branchUUID = user.branchUUID ?: return@launch
                val shift =
                    shift.value ?: EmployeeShift(
                        userUUID = user.uuid,
                        branchUUID = branchUUID,
                        username = user.name,
                        shift = emptyList()
                    )
                if (shift.shift.contains(_state.value.today)) return@launch
                val modified =
                    shift.copy(
                        shift = shift.shift + _state.value.today
                    )
                when (val result = upsertShift(modified)) {
                    is Either.Left -> Log.d("TAG", result.value.toString())
                    is Either.Right -> Log.d("TAG", "Upsert Shift Success")
                }
            }
        }

        fun onProfileClicked() {
            viewModelScope.launch {
                _navigation.send(HomeNavigationTarget.Profile)
            }
        }
    }