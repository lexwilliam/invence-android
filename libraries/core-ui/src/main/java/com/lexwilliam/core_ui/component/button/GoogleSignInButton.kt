package com.lexwilliam.core_ui.component.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    InvenceOutlineButton(
        modifier = modifier,
        isLoading = isLoading,
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
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
                Text(text = "Sign in with Google", style = InvenceTheme.typography.bodyMedium)
            }
        }
    }
}