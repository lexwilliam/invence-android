package com.lexwilliam.core_ui.component.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lexwilliam.core_ui.theme.InvenceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SizeAwareDialog(
    onDismiss: () -> Unit,
    title: @Composable () -> Unit,
    titleLeading: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    isCompact: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    val sizeModifier by rememberUpdatedState(
        when (isCompact) {
            true -> Modifier.fillMaxSize()
            false -> Modifier.padding(vertical = 32.dp)
        }
    )

    val dialogShape by rememberUpdatedState(
        when (isCompact) {
            true -> RectangleShape
            false -> RoundedCornerShape(8.dp)
        }
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties =
            DialogProperties(
                usePlatformDefaultWidth = !isCompact,
                dismissOnClickOutside = false,
                dismissOnBackPress = false,
                decorFitsSystemWindows = !isCompact
            )
    ) {
        Column(
            modifier =
                sizeModifier
                    .clip(dialogShape)
                    .background(InvenceTheme.colors.neutral10)
                    .consumeWindowInsets(WindowInsets.ime)
                    .padding(bottom = 16.dp)
                    .navigationBarsPadding()
        ) {
            CenterAlignedTopAppBar(
                title = title,
                navigationIcon = titleLeading,
                actions = actions,
                scrollBehavior = scrollBehavior
            )
            content()
        }
    }
}