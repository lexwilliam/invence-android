package com.lexwilliam.invence.ui

import android.os.Build
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
    val hasBranchUUID by viewModel.hasBranchUUID.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = isLoggedIn, key2 = hasBranchUUID) {
        when {
            !isLoggedIn -> navController.navigate(Screen.LOGIN)
            isLoggedIn && !hasBranchUUID ->
                navController.navigate(
                    Screen.COMPANY_SEARCH
                )
            else -> navController.navigate(Screen.INVENTORY)
        }
    }

    RootNavGraph(
        navController = navController,
        lifecycleScope = lifecycleScope,
        googleAuthUiClient = googleAuthUiClient
    )
}