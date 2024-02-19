package com.lexwilliam.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun ShiftCalendar(modifier: Modifier = Modifier) {
    // TODO: Make the Shift Calendar, this is a placeholder for now
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(InvenceTheme.colors.secondary, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "SHIFT CALENDAR", style = InvenceTheme.typography.titleMedium)
    }
}