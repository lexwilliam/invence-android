package com.lexwilliam.auth.navigation

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.lexwilliam.auth.route.login.LoginRoute
import com.lexwilliam.auth.route.login.LoginUiEvent
import com.lexwilliam.auth.route.login.LoginViewModel
import com.lexwilliam.auth.util.GoogleAuthUiClient
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.core_ui.component.ObserveAsEvents
import kotlinx.coroutines.launch

fun NavGraphBuilder.loginNavigation(
    lifecycleScope: LifecycleCoroutineScope,
    googleAuthUiClient: GoogleAuthUiClient,
    toCompanySearch: () -> Unit
) {
    composable(route = Screen.LOGIN) {
        val viewModel: LoginViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        ObserveAsEvents(flow = viewModel.navigation) { target ->
            when (target) {
                LoginNavigationTarget.CompanySearch -> toCompanySearch()
            }
        }

        val launcher =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { result ->
                    if (result.resultCode == RESULT_OK) {
                        lifecycleScope.launch {
                            val signInResult =
                                googleAuthUiClient.signInWithIntent(
                                    intent = result.data ?: return@launch
                                )
                            viewModel.onEvent(LoginUiEvent.SignIn(signInResult))
                        }
                    }
                }
            )

        LaunchedEffect(key1 = state.isUserValid) {
            if (state.isUserValid) {
                viewModel.onEvent(LoginUiEvent.Success)
            }
        }

        LoginRoute(
            onGoogleSignInTapped = {
                lifecycleScope.launch {
                    val signInIntentSender = googleAuthUiClient.signIn()
                    launcher.launch(
                        IntentSenderRequest.Builder(
                            signInIntentSender ?: return@launch
                        ).build()
                    )
                }
            }
        )
    }
}