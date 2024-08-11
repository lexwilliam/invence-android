package com.lexwilliam.profile.route

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.component.button.InvenceTextButton
import com.lexwilliam.core_ui.component.button.defaults.InvenceButtonDefaults
import com.lexwilliam.core_ui.component.image.NetworkImage
import com.lexwilliam.core_ui.component.topbar.InvenceCenterAlignedTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.profile.navigation.ProfileNavigationTarget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel = hiltViewModel(),
    onBackStack: () -> Unit,
    toLogin: () -> Unit
) {
    val user by viewModel.user.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.navigation) {
        when (it) {
            is ProfileNavigationTarget.Login -> toLogin()
            ProfileNavigationTarget.BackStack -> onBackStack()
        }
    }

    Scaffold(
        topBar = {
            InvenceCenterAlignedTopBar(
                title = {
                    Text(text = "Profile")
                },
                navigationIcon = {
                    IconButton(onClick = onBackStack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "back icon")
                    }
                }
            )
        },
        containerColor = InvenceTheme.colors.neutral10
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NetworkImage(
                imagePath = user?.imageUrl,
                modifier =
                    Modifier
                        .size(128.dp)
                        .clip(CircleShape)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = user?.name ?: "", style = InvenceTheme.typography.titleMedium)
                Text(text = user?.email ?: "", style = InvenceTheme.typography.bodyMedium)
            }
            InvenceTextButton(
                onClick = {
                    viewModel.onLogoutClicked()
                },
                colors =
                    InvenceButtonDefaults.textButtonColors(
                        contentColor = InvenceTheme.colors.error
                    )
            ) {
                Text(
                    text = "Logout",
                    style = InvenceTheme.typography.bodyMedium
                )
            }
        }
    }
}