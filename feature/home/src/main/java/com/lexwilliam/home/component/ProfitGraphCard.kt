package com.lexwilliam.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.home.route.HomeUiState

@Composable
fun ProfitGraphCard(
    modifier: Modifier = Modifier,
    state: HomeUiState,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .height(240.dp)
                .background(InvenceTheme.colors.primary, RoundedCornerShape(16.dp))
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Analytics",
                style = InvenceTheme.typography.titleMedium,
                color = InvenceTheme.colors.neutral10
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    onPrevious()
                }) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "previous date",
                        tint = InvenceTheme.colors.neutral10
                    )
                }
                Text(
                    text = state.date.toString(),
                    style = InvenceTheme.typography.titleMedium,
                    color = InvenceTheme.colors.neutral10
                )
                IconButton(onClick = onNext) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "next date",
                        tint = InvenceTheme.colors.neutral10
                    )
                }
            }
        }
    }
}

@Composable
fun ProfitChart() {
}