package com.lexwilliam.analytics.route

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.core_ui.component.navigation.InvenceResponsiveNavigation
import com.lexwilliam.core_ui.component.navigation.InvenceResponsiveTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsRoute(onDrawerNavigation: (String) -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    InvenceResponsiveNavigation(
        currentScreen = Screen.ANALYTICS,
        drawerState = drawerState,
        onNavigationItemClick = {
            onDrawerNavigation(it)
        }
    ) {
        Scaffold(
            containerColor = InvenceTheme.colors.neutral10,
            topBar = {
                InvenceResponsiveTopBar(
                    title = "Analytics",
                    drawerState = drawerState
                )
            }
        ) { innerPadding ->
            Column(
                modifier =
                    Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(40.dp),
                    imageVector = Icons.Default.BarChart,
                    contentDescription = "BarChart"
                )

                Text(
                    "Analytics Feature is Coming Soon",
                    style = InvenceTheme.typography.titleMedium
                )
            }
        }
    }
}