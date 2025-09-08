package com.lexwilliam.core_ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/**
 * Utility functions for device detection and responsive design
 */

@Composable
fun isTablet(): Boolean {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val widthDp = configuration.screenWidthDp.dp
    val heightDp = configuration.screenHeightDp.dp

    // Consider it a tablet if the smallest dimension is at least 600dp
    val smallestWidth = minOf(widthDp, heightDp)
    return smallestWidth >= 600.dp
}

@Composable
fun isLandscape(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
}

@Composable
fun isTabletLandscape(): Boolean {
    return isTablet() && isLandscape()
}

@Composable
fun isTabletPortrait(): Boolean {
    return isTablet() && !isLandscape()
}

/**
 * Determines the appropriate navigation type based on device and orientation
 */
@Composable
fun getNavigationType(): NavigationType {
    return when {
        isTabletLandscape() -> NavigationType.PERMANENT_SIDE_MENU
        isTabletPortrait() -> NavigationType.NAVIGATION_RAIL
        else -> NavigationType.NAVIGATION_DRAWER
    }
}

enum class NavigationType {
    NAVIGATION_DRAWER,
    NAVIGATION_RAIL,
    PERMANENT_SIDE_MENU
}