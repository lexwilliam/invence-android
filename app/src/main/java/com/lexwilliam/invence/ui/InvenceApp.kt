package com.lexwilliam.invence.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun InvenceApp(
    viewModel: AppViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val destination by viewModel.destination.collectAsStateWithLifecycle()

    if (!isLoading) {
        RootNavGraph(
            navController = navController,
            startDestination = destination
        )
    }
}