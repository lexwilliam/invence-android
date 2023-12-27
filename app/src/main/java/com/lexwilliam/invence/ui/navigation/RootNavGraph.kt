package com.lexwilliam.invence.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.lexwilliam.auth.navigation.loginNavigation
import com.lexwilliam.auth.util.GoogleAuthUiClient
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.inventory.navigation.inventoryNavigation
import com.lexwilliam.inventory.navigation.navigateToInventory

@Composable
fun RootNavGraph(
    startDestination: String = Screen.LOGIN,
    navController: NavHostController = rememberNavController(),
    lifecycleScope: LifecycleCoroutineScope,
    googleAuthUiClient: GoogleAuthUiClient,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        loginNavigation(
            lifecycleScope = lifecycleScope,
            googleAuthUiClient = googleAuthUiClient,
            toInventory = navController::navigateToInventory,
        )
        inventoryNavigation()
    }
}
