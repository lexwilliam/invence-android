package com.lexwilliam.product.route.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.core.extensions.toCurrency
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.component.button.InvencePrimaryButton
import com.lexwilliam.core_ui.component.chip.InvenceChip
import com.lexwilliam.core_ui.component.image.NetworkImage
import com.lexwilliam.core_ui.component.textfield.InvenceOutlineTextField
import com.lexwilliam.core_ui.component.topbar.InvenceTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.product.component.ProductItemCard
import com.lexwilliam.product.component.RestockDialog
import com.lexwilliam.product.navigation.ProductDetailNavigationTarget
import com.lexwilliam.product.route.detail.dialog.RestockDialogEvent

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProductDetailRoute(
    viewModel: ProductDetailViewModel = hiltViewModel(),
    onBackStack: () -> Unit,
    toProductForm: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val product by viewModel.product.collectAsStateWithLifecycle()
    val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()

    ObserveAsEvents(flow = viewModel.navigation) { target ->
        when (target) {
            ProductDetailNavigationTarget.BackStack -> onBackStack()
            is ProductDetailNavigationTarget.ProductForm ->
                toProductForm(target.productUUID)
        }
    }

    dialogState?.let { state ->
        RestockDialog(
            buyPrice = state.buyPrice,
            onBuyPriceChanged = { viewModel.onDialogEvent(RestockDialogEvent.BuyPriceChanged(it)) },
            quantity = state.quantity,
            onQuantityChanged = { viewModel.onDialogEvent(RestockDialogEvent.QuantityChanged(it)) },
            onDismiss = { viewModel.onDialogEvent(RestockDialogEvent.Dismiss) },
            onConfirm = { viewModel.onDialogEvent(RestockDialogEvent.Confirm) }
        )
    }

    Scaffold(
        containerColor = InvenceTheme.colors.neutral10,
        topBar = {
            InvenceTopBar(
                title = {
                    Text(
                        text = "Product Detail",
                        style = InvenceTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.onEvent(ProductDetailUiEvent.BackStackClicked) }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "nav back stack")
                    }
                },
                actions = {
                    Row {
                        IconButton(
                            onClick = { viewModel.onEvent(ProductDetailUiEvent.DeleteIconClicked) }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "delete product icon")
                        }
                        IconButton(
                            onClick = { viewModel.onEvent(ProductDetailUiEvent.EditIconClicked) }
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "edit product icon")
                        }
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Quantity: ${product.items.sumOf { it.quantity }}",
                    style = InvenceTheme.typography.bodyMedium
                )
                InvencePrimaryButton(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    onClick = {
                        viewModel.onEvent(ProductDetailUiEvent.RestockClicked)
                    }
                ) {
                    Text(
                        text = "Restock",
                        style = InvenceTheme.typography.labelLarge
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
        ) {
            NetworkImage(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                imagePath = product.imagePath
            )
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InvenceChip(
                    onClick = { },
                    label = {
                        Text(
                            text = product.categoryName,
                            style = InvenceTheme.typography.labelMedium,
                            color = InvenceTheme.colors.primary
                        )
                    }
                )
                Text(text = product.name, style = InvenceTheme.typography.titleLarge)
                Column {
                    Text(
                        text = "Sell Price",
                        style = InvenceTheme.typography.titleSmall
                    )
                    InvenceOutlineTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = product.sellPrice.toCurrency(),
                        onValueChange = {},
                        placeholder = {
                            Text(
                                text = "0.0",
                                style = InvenceTheme.typography.bodyLarge
                            )
                        },
                        readOnly = true
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Items", style = InvenceTheme.typography.titleMedium)
                        IconButton(
                            onClick = { viewModel.onEvent(ProductDetailUiEvent.ItemExpanded) }
                        ) {
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = "expand items content icon"
                            )
                        }
                    }
                    AnimatedVisibility(visible = uiState.itemExpanded) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            product.items.forEach { item ->
                                ProductItemCard(
                                    itemId = item.itemId,
                                    buyPrice = item.buyPrice.toString(),
                                    quantity = item.quantity.toString()
                                )
                            }
                        }
                    }
                }
                Text(text = "Description", style = InvenceTheme.typography.titleMedium)
                Text(text = product.description, style = InvenceTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Absolute.Right
                ) {
                    IconButton(
                        onClick = { viewModel.onEvent(ProductDetailUiEvent.CopyDescription) }
                    ) {
                        Icon(
                            Icons.Default.CopyAll,
                            contentDescription = "copy description icon"
                        )
                    }
                }
            }
        }
    }
}