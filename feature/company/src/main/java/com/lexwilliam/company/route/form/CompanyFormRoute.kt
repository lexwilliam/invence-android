package com.lexwilliam.company.route.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.branch.model.Branch
import com.lexwilliam.core_ui.component.button.InvenceFloatingActionButton
import com.lexwilliam.core_ui.component.card.ColumnCardWithImage
import com.lexwilliam.core_ui.component.textfield.InvenceOutlineTextField
import com.lexwilliam.core_ui.extension.dashedBorder
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun CompanyFormRoute(
    viewModel: CompanyFormViewModel = hiltViewModel(),
    toInventory: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = InvenceTheme.colors.primary,
        floatingActionButton = {
            InvenceFloatingActionButton(containerColor = InvenceTheme.colors.secondary, onClick = { viewModel.onEvent(CompanyFormUiEvent.StepChanged(uiState.step + 1)) }) {
                Icon(Icons.Default.NavigateNext, contentDescription = "next page icon", tint = InvenceTheme.colors.neutral10)
            }
        }
    ) { innerPadding ->
        when (uiState.step) {
            1 -> CompanyFormNamePage(
                modifier = Modifier.padding(innerPadding),
                uiState = uiState,
                onEvent = viewModel::onEvent
            )
            2 -> CompanyFormBranchPage(
                modifier = Modifier.padding(innerPadding),
                uiState = uiState,
                onEvent = viewModel::onEvent
            )
        }
    }
}

@Composable
fun CompanyFormNamePage(
    modifier: Modifier = Modifier,
    uiState: CompanyFormUiState,
    onEvent: (CompanyFormUiEvent) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to the Warehouse!", style = InvenceTheme.typography.titleLarge, color = InvenceTheme.colors.neutral10)
        Text(
            text = "Step into our digital warehouse! We need a few details to set up your company's space. Think of it as your backstage pass to inventory greatness.",
            style = InvenceTheme.typography.bodySmall, color = InvenceTheme.colors.neutral10
        )
        InvenceOutlineTextField(
            value = uiState.companyName,
            onValueChange = { onEvent(CompanyFormUiEvent.NameChanged(it)) },
            placeholder = { Text(text = "Your Business Alias", style = InvenceTheme.typography.bodyMedium)}
        )
    }
}

@Composable
fun CompanyFormBranchPage(
    modifier: Modifier = Modifier,
    uiState: CompanyFormUiState,
    onEvent: (CompanyFormUiEvent) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Branch Integration – Weave Your Inventory Network", style = InvenceTheme.typography.titleLarge, color = InvenceTheme.colors.neutral10)
        Text(
            text = "Let's weave the threads of your inventory network! Does your company have branches? Share the details, and let's make your inventory connections stronger",
            style = InvenceTheme.typography.bodySmall, color = InvenceTheme.colors.neutral10
        )
    }
}

@Composable
fun CompanyFormBranchList(
    branchList: List<Branch>,
    onAddBranch: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        items(items = branchList) { branch ->
            ColumnCardWithImage(
                modifier = Modifier.fillMaxWidth(),
                imagePath = branch.logoUrl,
            ) {
                Text(text = branch.name, style = InvenceTheme.typography.bodyMedium, color = InvenceTheme.colors.neutral10)
            }
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = onAddBranch
                    ),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .defaultMinSize(minWidth = 120.dp, minHeight = 120.dp)
                        .dashedBorder(
                            color = InvenceTheme.colors.neutral50,
                            shape = RoundedCornerShape(16.dp)
                        ),
                ) {
                    Icon(modifier = Modifier.size(24.dp), imageVector = Icons.Default.Add, contentDescription = "add branch icon", tint = InvenceTheme.colors.neutral50)
                }
                Text(text = "Add branch", style = InvenceTheme.typography.bodyMedium, color = InvenceTheme.colors.neutral50)
            }
        }
    }
}

//@Composable
//fun CompanyFormEmployee() {
//
//}