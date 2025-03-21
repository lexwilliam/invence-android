package com.lexwilliam.invence.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.lexwilliam.core_ui.theme.InvenceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen().apply {
            setKeepOnScreenCondition { viewModel.isLoading.value }
        }

        setContent {
            InvenceTheme {
                Surface(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .systemBarsPadding()
                            .statusBarsPadding(),
                    color = InvenceTheme.colors.neutral10
                ) {
                    InvenceApp()
                }
            }
        }
    }
}