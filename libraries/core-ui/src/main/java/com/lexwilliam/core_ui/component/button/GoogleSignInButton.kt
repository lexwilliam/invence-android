package com.lexwilliam.core_ui.component.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lexwilliam.core_ui.R
import com.lexwilliam.core_ui.component.loading.InvenceCircularProgressIndicator
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun GoogleSignInButton(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        enabled = !isLoading,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = InvenceTheme.colors.neutral10,
                contentColor = InvenceTheme.colors.primary,
                disabledContainerColor = InvenceTheme.colors.neutral60,
                disabledContentColor = InvenceTheme.colors.neutral30
            ),
        onClick = onClick
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoading) {
                InvenceCircularProgressIndicator(
                    color = InvenceTheme.colors.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text("Loading...", style = InvenceTheme.typography.bodyMedium)
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.google),
                    tint = InvenceTheme.colors.primary,
                    contentDescription = "google logo"
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(text = "Sign in with Google", style = InvenceTheme.typography.bodyMedium)
            }
        }
    }
}