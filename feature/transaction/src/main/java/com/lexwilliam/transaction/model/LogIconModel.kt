package com.lexwilliam.transaction.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Update
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.log.model.DataLog

data class LogIconModel(
    val icon: ImageVector,
    val containerColor: Color,
    val contentColor: Color
)

@Composable
fun getLogIcon(log: DataLog): LogIconModel {
    return when {
        log.add != null ->
            LogIconModel(
                icon = Icons.Default.Add,
                containerColor = InvenceTheme.colors.primary,
                contentColor = InvenceTheme.colors.neutral10
            )
        log.update != null ->
            LogIconModel(
                icon = Icons.Default.Update,
                containerColor = InvenceTheme.colors.primary,
                contentColor = InvenceTheme.colors.secondary
            )
        log.delete != null ->
            LogIconModel(
                icon = Icons.Default.Delete,
                containerColor = InvenceTheme.colors.primary,
                contentColor = InvenceTheme.colors.secondary
            )
        log.sell != null ->
            LogIconModel(
                icon = Icons.Default.AttachMoney,
                containerColor = InvenceTheme.colors.secondary,
                contentColor = InvenceTheme.colors.primary
            )
        log.restock != null ->
            LogIconModel(
                icon = Icons.Default.LocalShipping,
                containerColor = InvenceTheme.colors.secondary,
                contentColor = InvenceTheme.colors.primary
            )
        else ->
            LogIconModel(
                icon = Icons.Default.Error,
                containerColor = InvenceTheme.colors.neutral10,
                contentColor = InvenceTheme.colors.error
            )
    }
}