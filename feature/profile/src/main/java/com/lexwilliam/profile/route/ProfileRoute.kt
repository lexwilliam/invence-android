package com.lexwilliam.profile.route

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.component.button.InvenceTextButton
import com.lexwilliam.core_ui.component.button.defaults.InvenceButtonDefaults
import com.lexwilliam.core_ui.component.dialog.InvenceAlertDialog
import com.lexwilliam.core_ui.component.image.NetworkImage
import com.lexwilliam.core_ui.component.navigation.InvenceResponsiveNavigation
import com.lexwilliam.core_ui.component.topbar.InvenceCenterAlignedTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.profile.navigation.ProfileNavigationTarget
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel = hiltViewModel(),
    toLogin: () -> Unit,
    onDrawerNavigation: (String) -> Unit
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val isLogoutShowing by viewModel.isLogoutShowing.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.navigation) {
        when (it) {
            is ProfileNavigationTarget.Login -> toLogin()
        }
    }

    if (isLogoutShowing) {
        InvenceAlertDialog(
            title = "Logout",
            description = "Are you sure you want to logout?",
            onDismissRequest = { viewModel.onLogoutDismissed() },
            confirmButton = {
                InvenceTextButton(
                    onClick = { viewModel.onLogoutConfirmed() },
                    colors =
                        InvenceButtonDefaults.textButtonColors(
                            contentColor = InvenceTheme.colors.error
                        )
                ) {
                    Text("Logout", style = InvenceTheme.typography.bodyMedium)
                }
            },
            dismissButton = {
                InvenceTextButton(
                    onClick = { viewModel.onLogoutDismissed() }
                ) {
                    Text("Cancel", style = InvenceTheme.typography.bodyMedium)
                }
            }
        )
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    InvenceResponsiveNavigation(
        currentScreen = Screen.PROFILE,
        drawerState = drawerState,
        onNavigationItemClick = onDrawerNavigation
    ) {
        Scaffold(
            topBar = {
                InvenceCenterAlignedTopBar(
                    title = {
                        Text(text = "Profile", style = InvenceTheme.typography.titleMedium)
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "menu icon")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                viewModel.onLogoutClicked()
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "logout icon",
                                tint = InvenceTheme.colors.error
                            )
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
                        .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.height(16.dp))
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
                HorizontalDivider(
                    thickness = 4.dp,
                    color = InvenceTheme.colors.neutral20,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }
    }
}