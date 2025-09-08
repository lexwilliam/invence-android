package com.lexwilliam.inventory.route

import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.core_ui.R
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.component.button.InvenceFloatingActionButton
import com.lexwilliam.core_ui.component.chip.InvenceFilterChip
import com.lexwilliam.core_ui.component.navigation.InvenceResponsiveNavigation
import com.lexwilliam.core_ui.component.navigation.InvenceResponsiveTopBar
import com.lexwilliam.core_ui.component.textfield.InvenceSearchTextField
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.inventory.component.InventoryColumnCard
import com.lexwilliam.inventory.navigation.InventoryNavigationTarget
import com.lexwilliam.inventory.scan.InventoryScanDialog

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun InventoryRoute(
    viewModel: InventoryViewModel = hiltViewModel(),
    toProductForm: (String?) -> Unit,
    toProductDetail: (String) -> Unit,
    toCategory: () -> Unit,
    onDrawerNavigation: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val uiProducts by viewModel.uiProducts.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.navigation) { target ->
        when (target) {
            is InventoryNavigationTarget.ProductForm -> toProductForm(target.productUUID)
            is InventoryNavigationTarget.ProductDetail -> toProductDetail(target.productUUID)
            InventoryNavigationTarget.Category -> toCategory()
        }
    }

    if (uiState.isScanBarcodeShowing) {
        InventoryScanDialog(viewModel = viewModel)
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    InvenceResponsiveNavigation(
        currentScreen = Screen.INVENTORY,
        drawerState = drawerState,
        onNavigationItemClick = onDrawerNavigation
    ) {
        Scaffold(
            topBar = {
                InvenceResponsiveTopBar(
                    title = "Inventory",
                    drawerState = drawerState
                )
            },
            floatingActionButton = {
                InvenceFloatingActionButton(
                    onClick = { viewModel.onEvent(InventoryUiEvent.FabClicked) }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add),
                        contentDescription = "plus fab icon"
                    )
                }
            },
            containerColor = InvenceTheme.colors.neutral10
        ) { innerPadding ->
            LazyColumn(
                modifier =
                    Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InvenceSearchTextField(
                            modifier =
                                Modifier
                                    .weight(1f),
                            value = uiState.query.query,
                            onValueChange = {
                                viewModel.onEvent(
                                    InventoryUiEvent.QueryChanged(it)
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Search",
                                    style = InvenceTheme.typography.bodyLarge
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.search),
                                    contentDescription = "search icon",
                                    tint = InvenceTheme.colors.primary
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        viewModel.onEvent(
                                            InventoryUiEvent.BarcodeScannerClicked
                                        )
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.barcode_scanner),
                                        contentDescription = "barcode scan icon",
                                        tint = InvenceTheme.colors.primary
                                    )
                                }
                            },
                            singleLine = true
                        )
                    }
                }
                item {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Spacer(modifier = Modifier.width(0.dp))
                            InvenceFilterChip(
                                selected = uiState.query.categoryUUID == null,
                                onClick = {
                                    viewModel.onEvent(
                                        InventoryUiEvent.CategoryClicked(null)
                                    )
                                },
                                label = { Text("All", style = InvenceTheme.typography.labelLarge) }
                            )
                            categories.forEach { category ->
                                InvenceFilterChip(
                                    selected = uiState.query.categoryUUID == category.uuid,
                                    onClick = {
                                        viewModel.onEvent(
                                            InventoryUiEvent.CategoryClicked(category)
                                        )
                                    },
                                    label = {
                                        Text(
                                            category.name,
                                            style = InvenceTheme.typography.labelLarge
                                        )
                                    }
                                )
                            }
                        }
                        Icon(
                            modifier =
                                Modifier
                                    .padding(horizontal = 16.dp)
                                    .clickable {
                                        viewModel.onEvent(
                                            InventoryUiEvent.CategorySettingClicked
                                        )
                                    },
                            imageVector = Icons.Default.Settings,
                            contentDescription = "category settings icon"
                        )
                    }
                }
                if (uiProducts.isNotEmpty()) {
                    items(items = uiProducts) { product ->
                        InventoryColumnCard(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            product = product.product,
                            onClick = {
                                viewModel.onEvent(InventoryUiEvent.ProductClicked(product.product))
                            }
                        )
                    }
                } else {
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No product found",
                                style = InvenceTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}