package com.lexwilliam.auth.route.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lexwilliam.core_ui.component.brand.InvenceLogoText
import com.lexwilliam.core_ui.component.button.GoogleSignInButton
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun LoginRoute(onGoogleSignInTapped: () -> Unit) {
    Scaffold(
        modifier =
            Modifier
                .fillMaxSize(),
        containerColor = InvenceTheme.colors.secondary,
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InvenceLogoText()
                Text(
                    text =
                        "Streamline inventory, warehousing, and boost efficiency with user-friendly " +
                            "controls for real-time tracking and order fulfillment.",
                    style = InvenceTheme.typography.bodyMedium,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                GoogleSignInButton(modifier = Modifier.fillMaxWidth(), onClick = onGoogleSignInTapped)
            }
        }
    }
}
