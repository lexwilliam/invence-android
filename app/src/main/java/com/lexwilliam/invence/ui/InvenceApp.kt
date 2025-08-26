package com.lexwilliam.invence.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lexwilliam.auth.route.login.navigation.navigateToLogin
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.core.session.Session
import com.lexwilliam.inventory.navigation.navigateToInventory

@Composable
fun InvenceApp(
    viewModel: AppViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

    if (isLoggedIn != null) {
        LaunchedEffect(isLoggedIn) {
            isLoggedIn?.let {
                when (it) {
                    is Session.Authenticated -> navController.navigateToInventory()
                    Session.Unauthenticated -> navController.navigateToLogin()
                }
            }
        }
    }

    RootNavGraph(
        navController = navController,
        startDestination = Screen.SPLASH
    )
}