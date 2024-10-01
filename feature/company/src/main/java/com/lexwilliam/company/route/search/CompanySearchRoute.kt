package com.lexwilliam.company.route.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.company.navigation.CompanySearchNavigationTarget
import com.lexwilliam.company.route.search.dialog.SelectBranchDialog
import com.lexwilliam.core_ui.R
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.component.button.InvenceOutlineButton
import com.lexwilliam.core_ui.component.textfield.InvenceSearchTextField
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun CompanySearchRoute(
    viewModel: CompanySearchViewModel = hiltViewModel(),
    toCompanyForm: () -> Unit,
    toHome: () -> Unit
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
                    .background(InvenceTheme.colors.secondary),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = "Set Your Company",
                style = InvenceTheme.typography.brand,
                color = InvenceTheme.colors.primary
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Search your company", style = InvenceTheme.typography.labelLarge)
                InvenceSearchTextField(
                    modifier =
                        Modifier
                            .width(300.dp),
                    value = uiState.query,
                    onValueChange = { viewModel.onEvent(CompanySearchUiEvent.QueryChanged(it)) },
                    placeholder = {
                        Text(
                            text = "Search",
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
            Row(
                modifier = Modifier.width(300.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(InvenceTheme.colors.primary)
                )
                Text(text = "or", style = InvenceTheme.typography.bodyMedium)
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(InvenceTheme.colors.primary)
                )
            }
            Column(
                modifier = Modifier.width(300.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "If you don't have a company yet",
                    style = InvenceTheme.typography.labelLarge
                )
                InvenceOutlineButton(
                    modifier =
                        Modifier
                            .width(300.dp),
                    onClick = { viewModel.onEvent(CompanySearchUiEvent.CreateCompanyClicked) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "add company icon")
                        Text(
                            text = "Create new company",
                            style = InvenceTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}