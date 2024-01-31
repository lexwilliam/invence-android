package com.lexwilliam.core_ui.component.button

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
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun GoogleSignInButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = InvenceTheme.colors.neutral10,
                contentColor = InvenceTheme.colors.primary,
                disabledContainerColor = InvenceTheme.colors.neutral60,
                disabledContentColor = InvenceTheme.colors.neutral30
            ),
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = R.drawable.google),
            tint = InvenceTheme.colors.primary,
            contentDescription = "google logo"
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = "Sign in with Google", style = InvenceTheme.typography.bodyMedium)
    }
}