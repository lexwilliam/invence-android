package com.lexwilliam.core_ui.component.topbar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.lexwilliam.core_ui.theme.InvenceTheme

object InvenceTopAppBarDefaults {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun invenceCenterAlignedTopAppBarColors(
        containerColor: Color = InvenceTheme.colors.neutral10,
        scrolledContainerColor: Color = InvenceTheme.colors.neutral10,
        navigationIconContentColor: Color = InvenceTheme.colors.primary,
        titleContentColor: Color = InvenceTheme.colors.primary,
        actionIconContentColor: Color = InvenceTheme.colors.primary
    ): TopAppBarColors =
        TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor,
            scrolledContainerColor,
            navigationIconContentColor,
            titleContentColor,
            actionIconContentColor
        )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun invenceTopAppBarColors(
        containerColor: Color = InvenceTheme.colors.neutral10,
        scrolledContainerColor: Color = InvenceTheme.colors.neutral10,
        navigationIconContentColor: Color = InvenceTheme.colors.primary,
        titleContentColor: Color = InvenceTheme.colors.primary,
        actionIconContentColor: Color = InvenceTheme.colors.primary
    ): TopAppBarColors =
        TopAppBarDefaults.topAppBarColors(
            containerColor,
            scrolledContainerColor,
            navigationIconContentColor,
            titleContentColor,
            actionIconContentColor
        )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun invenceBarcodeTopAppBarColors(
        containerColor: Color = Color.Transparent,
        scrolledContainerColor: Color = Color.Transparent,
        navigationIconContentColor: Color = Color.Transparent,
        titleContentColor: Color = Color.Transparent,
        actionIconContentColor: Color = Color.Transparent
    ): TopAppBarColors =
        TopAppBarDefaults.topAppBarColors(
            containerColor,
            scrolledContainerColor,
            navigationIconContentColor,
            titleContentColor,
            actionIconContentColor
        )
}