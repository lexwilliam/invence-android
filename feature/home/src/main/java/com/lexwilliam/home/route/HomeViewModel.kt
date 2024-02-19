package com.lexwilliam.home.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexwilliam.home.navigation.HomeNavigationTarget
import com.lexwilliam.user.usecase.FetchUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val fetchUser: FetchUserUseCase
    ) : ViewModel() {
        private val _navigation = Channel<HomeNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        fun onHomeIconClicked(label: String) {
            viewModelScope.launch {
                when (label) {
                    "Inventory" -> _navigation.send(HomeNavigationTarget.Inventory)
                    "Order" -> _navigation.send(HomeNavigationTarget.Cart)
                    else -> {}
                }
            }
        }
    }