package com.lexwilliam.home.route

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lexwilliam.core.util.getGreetingString
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.component.topbar.InvenceTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.home.component.HomeIconButton
import com.lexwilliam.home.component.ShiftCalendar
import com.lexwilliam.home.model.homeIcons
import com.lexwilliam.home.navigation.HomeNavigationTarget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    toInventory: () -> Unit,
    toCart: () -> Unit
) {
    ObserveAsEvents(flow = viewModel.navigation) { target ->
        when (target) {
            HomeNavigationTarget.Inventory -> toInventory()
            HomeNavigationTarget.Cart -> toCart()
        }
    }

    Scaffold(
        topBar = {
            InvenceTopBar(
                title = {
                    Text(
                        text = getGreetingString(),
                        style = InvenceTheme.typography.titleMedium
                    )
                }
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                ShiftCalendar()
            }
            items(items = homeIcons) { model ->
                HomeIconButton(
                    onClick = { viewModel.onHomeIconClicked(model.label) },
                    icon = model.icon,
                    iconColor = InvenceTheme.colors.primary,
                    backgroundColor = InvenceTheme.colors.secondary,
                    label = model.label
                )
            }
        }
    }
}