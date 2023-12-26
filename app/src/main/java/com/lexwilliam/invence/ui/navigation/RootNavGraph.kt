package com.lexwilliam.invence.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.lexwilliam.auth.navigation.loginNavigation
import com.lexwilliam.auth.navigation.signUpNavigation
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.inventory.navigation.inventoryNavigation

@Composable
fun RootNavGraph(
    modifier: Modifier = Modifier,
    startDestination: String = Screen.LOGIN,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        loginNavigation()
        signUpNavigation()
        inventoryNavigation()
    }
}