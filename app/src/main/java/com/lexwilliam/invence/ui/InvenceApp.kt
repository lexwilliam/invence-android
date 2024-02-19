package com.lexwilliam.invence.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lexwilliam.auth.util.GoogleAuthUiClient
import com.lexwilliam.core.navigation.Screen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InvenceApp(
    viewModel: AppViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
    lifecycleScope: LifecycleCoroutineScope,
    googleAuthUiClient: GoogleAuthUiClient
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val branchUUID by viewModel.branchUUID.collectAsStateWithLifecycle()
    val userUUID by viewModel.userUUID.collectAsStateWithLifecycle()

    Log.d("TAG", isLoggedIn.toString())

    LaunchedEffect(key1 = isLoggedIn) {
        when (isLoggedIn) {
            AuthState.NO_ACCOUNT -> navController.navigate(Screen.LOGIN)
            AuthState.NO_BRANCH ->
                navController.navigate(
                    Screen.COMPANY_SEARCH
                )
            AuthState.NONE -> navController.navigate(Screen.SPLASH)
            else -> navController.navigate(Screen.HOME)
        }
    }

    RootNavGraph(
        navController = navController,
        lifecycleScope = lifecycleScope,
        googleAuthUiClient = googleAuthUiClient
    )
}