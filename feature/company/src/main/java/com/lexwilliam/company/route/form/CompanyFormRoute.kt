package com.lexwilliam.company.route.form

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.company.navigation.CompanyFormNavigationTarget
import com.lexwilliam.company.route.form.dialog.CompanyFormDialogEvent
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.component.button.InvenceFloatingActionButton
import com.lexwilliam.core_ui.component.button.InvenceRadioButton
import com.lexwilliam.core_ui.component.card.ColumnCardWithImage
import com.lexwilliam.core_ui.component.dialog.FormDialogWithImage
import com.lexwilliam.core_ui.component.textfield.InvenceOutlineTextField
import com.lexwilliam.core_ui.extension.dashedBorder
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun CompanyFormRoute(
    viewModel: CompanyFormViewModel = hiltViewModel(),
    toHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.navigation) { target ->
        when (target) {
            CompanyFormNavigationTarget.Home -> toHome()
        }
    }

    dialogState?.let { state ->
        FormDialogWithImage(
            onDismiss = { viewModel.onDialogEvent(CompanyFormDialogEvent.Dismiss) },
            image = state.bitmap,
            onImageChanged = { bmp ->
                viewModel.onDialogEvent(
                    CompanyFormDialogEvent.ImageChanged(bmp)
                )
            },
            title = "Add Branch",
            label = "Name",
            placeHolder = "Input branch name",
            name = state.name,
            onNameChanged = { viewModel.onDialogEvent(CompanyFormDialogEvent.NameChanged(it)) },
            onConfirm = { viewModel.onDialogEvent(CompanyFormDialogEvent.Confirm) }
        )
    }

    Scaffold(
        containerColor = InvenceTheme.colors.secondary,
        floatingActionButton = {
            InvenceFloatingActionButton(containerColor = InvenceTheme.colors.primary, onClick = {
                viewModel.onEvent(CompanyFormUiEvent.StepChanged(uiState.step + 1))
            }) {
                Icon(
                    Icons.Default.NavigateNext,
                    contentDescription = "next page icon",
                    tint = InvenceTheme.colors.neutral10
                )
            }
        }
    ) { innerPadding ->
        when (uiState.step) {
            1 ->
                CompanyFormNamePage(
                    modifier = Modifier.padding(innerPadding),
                    uiState = uiState,
                    onEvent = viewModel::onEvent
                )
            2 ->
                CompanyFormBranchPage(
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
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "Setup your company",
            style = InvenceTheme.typography.brand,
            color = InvenceTheme.colors.primary
        )
        Text(
            text = "Step 1: Set up your company name",
            style = InvenceTheme.typography.bodyMedium
        )
        InvenceOutlineTextField(
            value = uiState.companyName,
            onValueChange = { onEvent(CompanyFormUiEvent.NameChanged(it)) },
            placeholder = {
                Text(
                    text = "Your Business Alias",
                    style = InvenceTheme.typography.bodyMedium
                )
            }
        )
        Text(text = uiState.companyId, style = InvenceTheme.typography.labelLarge)
    }
}

@Composable
fun CompanyFormBranchPage(
    modifier: Modifier = Modifier,
    uiState: CompanyFormUiState,
    onEvent: (CompanyFormUiEvent) -> Unit
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "Branches",
            style = InvenceTheme.typography.brand,
            color = InvenceTheme.colors.primary
        )
        Text(
            text =
                "Step 2: Does your company have branches? You can add them here or later. " +
                    "\n\nBut make sure you have one branches at the start",
            style = InvenceTheme.typography.bodyMedium
        )
        CompanyFormBranchList(
            uiState = uiState,
            onEvent = onEvent
        )
    }
}

@Composable
fun CompanyFormBranchList(
    uiState: CompanyFormUiState,
    onEvent: (CompanyFormUiEvent) -> Unit
) {
    LazyColumn(
        modifier =
            Modifier
                .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = uiState.branchList) { branch ->
            ColumnCardWithImage(
                modifier = Modifier.fillMaxWidth(),
                imageModifier = Modifier.size(64.dp),
                imagePath = null
            ) {
                Row(
                    modifier =
                        Modifier
                            .height(64.dp)
                            .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = branch.name,
                        style = InvenceTheme.typography.titleMedium,
                        color = InvenceTheme.colors.primary
                    )
                    InvenceRadioButton(
                        selected = uiState.selectedBranch?.uuid == branch.uuid,
                        onClick = { onEvent(CompanyFormUiEvent.BranchSelected(branch)) }
                    )
                }
            }
        }
        item {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current,
                            onClick = { onEvent(CompanyFormUiEvent.AddBranch) }
                        ),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier =
                        Modifier
                            .defaultMinSize(minWidth = 64.dp, minHeight = 64.dp)
                            .dashedBorder(
                                color = InvenceTheme.colors.primary,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .background(
                                InvenceTheme.colors.neutral10,
                                shape = RoundedCornerShape(16.dp)
                            ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(36.dp),
                        imageVector = Icons.Default.Add,
                        contentDescription = "add branch icon",
                        tint = InvenceTheme.colors.primary
                    )
                }
                Text(
                    text = "Add branch",
                    style = InvenceTheme.typography.titleMedium,
                    color = InvenceTheme.colors.primary
                )
            }
        }
    }
}