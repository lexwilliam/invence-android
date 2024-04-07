package com.lexwilliam.company.route.search.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lexwilliam.company.model.CompanyBranch
import com.lexwilliam.company.route.search.CompanySearchUiEvent
import com.lexwilliam.core_ui.component.card.ColumnCardWithImage
import com.lexwilliam.core_ui.component.topbar.InvenceTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectBranchDialog(
    branches: List<CompanyBranch>,
    onEvent: (CompanySearchUiEvent) -> Unit
) {
    Dialog(
        onDismissRequest = { onEvent(CompanySearchUiEvent.DismissDialog) },
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnClickOutside = false,
                dismissOnBackPress = false,
                decorFitsSystemWindows = false
            )
    ) {
        Surface(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(InvenceTheme.colors.neutral10)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InvenceTopBar(
                    title = {
                        Text(
                            text = "Select Branch",
                            style = InvenceTheme.typography.titleMedium
                        )
                    },
                    actions = {
                        IconButton(onClick = { onEvent(CompanySearchUiEvent.DismissDialog) }) {
                            Icon(Icons.Default.Close, contentDescription = "dismiss dialog icon")
                        }
                    }
                )
                branches.forEach { branch ->
                    ColumnCardWithImage(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clickable { onEvent(CompanySearchUiEvent.BranchSelected(branch)) },
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
                                style = InvenceTheme.typography.bodyLarge,
                                color = InvenceTheme.colors.primary
                            )
                        }
                    }
                }
            }
        }
    }
}