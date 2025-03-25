package com.lexwilliam.invence.ui

import InvenceSnackbar
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.controller.SnackbarController
import com.lexwilliam.core_ui.theme.InvenceTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle =
                SystemBarStyle.light(
                    scrim = Color.WHITE,
                    darkScrim = Color.BLACK
                )
        )
        installSplashScreen().apply {
            setKeepOnScreenCondition { viewModel.isLoading.value }
        }

        setContent {
            InvenceTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                ObserveAsEvents(flow = SnackbarController.events, snackbarHostState) { events ->
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        val result =
                            snackbarHostState.showSnackbar(
                                "${events.type}_${events.message}",
                                actionLabel = events.action?.name,
                                duration = SnackbarDuration.Short
                            )

                        if (result == SnackbarResult.ActionPerformed) {
                            events.action?.action?.invoke()
                        }
                    }
                }
                Scaffold(
                    contentWindowInsets = WindowInsets(0.dp),
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier.padding(innerPadding),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        InvenceApp()
                        SnackbarHost(
                            modifier = Modifier.systemBarsPadding(),
                            hostState = snackbarHostState
                        ) { data ->
                            InvenceSnackbar(snackbarData = data)
                        }
                    }
                }
            }
        }
    }
}