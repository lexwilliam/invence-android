package com.lexwilliam.auth.route.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import arrow.optics.copy
import com.lexwilliam.auth.navigation.LoginNavigationTarget
import com.lexwilliam.auth.util.SignInResult
import com.lexwilliam.user.model.User
import com.lexwilliam.user.usecase.FetchUserUseCase
import com.lexwilliam.user.usecase.UpsertUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val upsertUser: UpsertUserUseCase,
        private val fetchUser: FetchUserUseCase
    ) : ViewModel() {
        private val _state = MutableStateFlow(LoginUiState())
        val state = _state.asStateFlow()

        private val _navigation = Channel<LoginNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        fun onEvent(event: LoginUiEvent) {
            when (event) {
                is LoginUiEvent.SignInWithGoogle -> handleSignIn(event.result)
                is LoginUiEvent.EmailChanged -> TODO()
                is LoginUiEvent.PasswordChanged -> TODO()
            }
        }

        private fun handleSignIn(result: SignInResult) {
            viewModelScope.launch {
                val errorMessage = result.errorMessage
                val userData = result.data
                when {
                    errorMessage != null -> _state.update { old -> old.copy(error = errorMessage) }
                    userData == null ->
                        _state.update {
                                old ->
                            old.copy(error = "Can't find user data")
                        }
                    else -> {
                        val user =
                            User(
                                uuid = userData.userId,
                                name = userData.username ?: "",
                                email = userData.email ?: "",
                                imageUrl = userData.profilePictureUrl,
                                createdAt = Clock.System.now()
                            )

                        val userDoc = fetchUser(user.uuid).getOrNull()
                        if (userDoc != null) {
                            if (userDoc.branchUUID != null) {
                                _navigation.send(LoginNavigationTarget.Home)
                            } else {
                                _navigation.send(LoginNavigationTarget.CompanySearch)
                            }
                            return@launch
                        }

                        when (val upsertResult = upsertUser(user)) {
                            is Either.Left ->
                                _state.update {
                                        old ->
                                    old.copy(error = "${upsertResult.value}: Insert User Failed")
                                }
                            is Either.Right -> {
                                if (upsertResult.value.branchUUID != null) {
                                    _navigation.send(LoginNavigationTarget.Home)
                                } else {
                                    _navigation.send(LoginNavigationTarget.CompanySearch)
                                }
                            }
                        }
                    }
                }
            }
        }
    }