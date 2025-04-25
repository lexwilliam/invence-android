package com.lexwilliam.company.route.my

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.core_ui.component.topbar.InvenceCenterAlignedTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCompanyRoute(
    onBackStack: () -> Unit,
    toCompanyMember: () -> Unit,
    viewModel: MyCompanyViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = InvenceTheme.colors.neutral10,
        topBar = {
            InvenceCenterAlignedTopBar(
                title = {
                    Text(text = "My Company", style = InvenceTheme.typography.titleMedium)
                },
                navigationIcon = {
                    IconButton(onClick = onBackStack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back icon")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(state.company?.name ?: "", style = InvenceTheme.typography.titleLarge)
            }
        }
    }
}