package com.lexwilliam.company.route.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.company.navigation.CompanySearchNavigationTarget
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.component.button.InvencePrimaryButton
import com.lexwilliam.core_ui.component.button.InvenceSecondaryButton
import com.lexwilliam.core_ui.component.textfield.InvenceSearchTextField
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun CompanySearchRoute(
    viewModel: CompanySearchViewModel = hiltViewModel(),
    toCompanyForm: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.navigation) { target ->
        when (target) {
            CompanySearchNavigationTarget.CompanyForm -> toCompanyForm()
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(InvenceTheme.colors.secondary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "Invence",
            style = InvenceTheme.typography.brand,
            color = InvenceTheme.colors.primary
        )
        InvenceSearchTextField(value = uiState.query, onValueChange = {
            viewModel.onEvent(CompanySearchUiEvent.QueryChanged(it))
        })
        InvencePrimaryButton(
            onClick = { viewModel.onEvent(CompanySearchUiEvent.CreateCompanyClicked) }
        ) {
            Text(text = "Confirm", style = InvenceTheme.typography.labelLarge)
        }
        Spacer(modifier = Modifier.size(16.dp))
        InvenceSecondaryButton(
            onClick = { viewModel.onEvent(CompanySearchUiEvent.CreateCompanyClicked) }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "add company icon")
                Text(text = "Create new company", style = InvenceTheme.typography.labelLarge)
            }
        }
    }
}