package com.lexwilliam.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun HomeIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector,
    iconColor: Color,
    backgroundColor: Color,
    label: String
) {
    Column(
        modifier =
            modifier
                .clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier =
                Modifier
                    .size(48.dp, 48.dp)
        ) {
            Box(
                modifier =
                    Modifier
                        .size(46.dp)
                        .background(backgroundColor, RoundedCornerShape(8.dp))
                        .align(Alignment.Center)
            )
            Icon(
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .size(36.dp)
                        .offset(4.dp, 6.dp),
                imageVector = icon,
                contentDescription = "feature icon",
                tint = iconColor
            )
        }
        Text(text = label, style = InvenceTheme.typography.labelMedium)
    }
}