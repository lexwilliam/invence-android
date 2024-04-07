package com.lexwilliam.invence.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lexwilliam.auth.util.GoogleAuthUiClient

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InvenceApp(
    viewModel: AppViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
    lifecycleScope: LifecycleCoroutineScope,
    googleAuthUiClient: GoogleAuthUiClient
) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val destination by viewModel.destination.collectAsStateWithLifecycle()

    if (!isLoading) {
        RootNavGraph(
            navController = navController,
            lifecycleScope = lifecycleScope,
            googleAuthUiClient = googleAuthUiClient,
            startDestination = destination
        )
    }
}