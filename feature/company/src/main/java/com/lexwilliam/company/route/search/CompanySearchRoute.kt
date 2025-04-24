package com.lexwilliam.company.route.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.company.navigation.CompanySearchNavigationTarget
import com.lexwilliam.company.route.search.dialog.SelectBranchDialog
import com.lexwilliam.core_ui.R
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.component.button.InvencePrimaryButton
import com.lexwilliam.core_ui.component.button.InvenceTextButton
import com.lexwilliam.core_ui.component.textfield.InvenceOutlineTextField
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun CompanySearchRoute(
    viewModel: CompanySearchViewModel = hiltViewModel(),
    toCompanyForm: () -> Unit,
    toHome: () -> Unit,
    toLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.navigation) { target ->
        when (target) {
            CompanySearchNavigationTarget.CompanyForm -> toCompanyForm()
            CompanySearchNavigationTarget.Home -> toHome()
        }
    }

    if (uiState.isDialogShown) {
        SelectBranchDialog(
            branches = uiState.branches,
            onEvent = viewModel::onEvent
        )
    }

    if (uiState.showSearch) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(InvenceTheme.colors.secondary)
                    .padding(top = 100.dp)
                    .padding(16.dp)
                    .systemBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Icon(
                modifier =
                    Modifier
                        .clip(CircleShape)
                        .clickable { toLogin() },
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "login icon"
            )
            Text(
                text = "Company",
                style = InvenceTheme.typography.brand,
                color = InvenceTheme.colors.primary
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Search your company", style = InvenceTheme.typography.labelLarge)
                InvenceOutlineTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.query,
                    onValueChange = { viewModel.onEvent(CompanySearchUiEvent.QueryChanged(it)) },
                    placeholder = {
                        Text(
                            text = "company_name#123",
                            style = InvenceTheme.typography.bodyLarge
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = "search icon",
                            tint = InvenceTheme.colors.primary
                        )
                    },
                    singleLine = true
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InvencePrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.onEvent(CompanySearchUiEvent.ConfirmClicked)
                    }
                ) {
                    Text(
                        text = "Search",
                        style = InvenceTheme.typography.labelLarge
                    )
                }
                InvenceTextButton(
                    onClick = {
                        viewModel.onEvent(CompanySearchUiEvent.CreateCompanyClicked)
                    }
                ) {
                    Text(
                        text = "Don't have a company yet ? Create one",
                        style = InvenceTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}