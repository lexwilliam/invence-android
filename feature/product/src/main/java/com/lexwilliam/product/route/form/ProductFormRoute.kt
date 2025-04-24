package com.lexwilliam.product.route.form

import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.category.dialog.SelectCategoryDialog
import com.lexwilliam.core_ui.R
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.component.button.InvenceOutlineButton
import com.lexwilliam.core_ui.component.button.InvencePrimaryButton
import com.lexwilliam.core_ui.component.button.InvenceSecondaryButton
import com.lexwilliam.core_ui.component.image.InputImage
import com.lexwilliam.core_ui.component.textfield.InvenceOutlineTextField
import com.lexwilliam.core_ui.component.topbar.InvenceCenterAlignedTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.product.component.ProductItemCard
import com.lexwilliam.product.navigation.ProductFormNavigationTarget
import com.lexwilliam.product.route.form.scan.ProductScanDialog

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormRoute(
    viewModel: ProductFormViewModel = hiltViewModel(),
    onBackStack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val productUUID by viewModel.productUUID.collectAsStateWithLifecycle(initialValue = null)
    val product by viewModel.product.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.navigation) { target ->
        when (target) {
            is ProductFormNavigationTarget.BackStack -> onBackStack()
        }
    }

    if (uiState.isSelectCategoryShowing) {
        SelectCategoryDialog(
            onDismiss = { viewModel.onEvent(ProductFormUiEvent.CategoryDismiss) },
            categories = categories,
            onCategoryClicked = {
                viewModel.onEvent(ProductFormUiEvent.CategorySelected(it))
            },
            onFormConfirm = { name, image ->
                viewModel.onEvent(ProductFormUiEvent.AddCategory(name, image))
            }
        )
    }

    if (uiState.isScanBarcodeShowing) {
        ProductScanDialog(viewModel = viewModel)
    }

    Scaffold(
        containerColor = InvenceTheme.colors.neutral10,
        topBar = {
            InvenceCenterAlignedTopBar(
                title = {
                    Text(
                        text = "Product Form",
                        style = InvenceTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.onEvent(ProductFormUiEvent.BackStackClicked) }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "nav back stack")
                    }
                }
            )
        },
        bottomBar = {
            Row {
                InvencePrimaryButton(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    enabled = uiState.isValid,
                    isLoading = uiState.isLoading,
                    onClick = {
                        viewModel.onEvent(ProductFormUiEvent.SaveClicked)
                    }
                ) {
                    Text(
                        text = "Save",
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
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InputImage(
                imageModifier =
                    Modifier
                        .padding(horizontal = 16.dp)
                        .size(128.dp),
                image = uiState.image,
                onImageChanged = { image ->
                    viewModel.onEvent(ProductFormUiEvent.InputImageChanged(image))
                }
            )
            Row(
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(0.2f),
                    text = "SKU",
                    style = InvenceTheme.typography.titleSmall
                )
                InvenceOutlineTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value =
                        uiState.sku
                            ?: "",
                    onValueChange = {
                        viewModel.onEvent(ProductFormUiEvent.SkuChanged(it))
                    },
                    placeholder = {
                        Text(
                            text = "Input SKU code",
                            style = InvenceTheme.typography.bodyMedium
                        )
                    },
                    trailingIcon = {
                        AssistChip(
                            modifier = Modifier.padding(end = 8.dp),
                            onClick = { viewModel.onEvent(ProductFormUiEvent.GenerateSkuClicked) },
                            label = {
                                Text(
                                    text = "Generate",
                                    style = InvenceTheme.typography.labelLarge
                                )
                            }
                        )
                    },
                    singleLine = true,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                )
            }
            Row(
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(0.2f),
                    text = "UPC",
                    style = InvenceTheme.typography.titleSmall
                )
                InvenceOutlineTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value =
                        uiState.upc ?: "",
                    onValueChange = {
                        viewModel.onEvent(ProductFormUiEvent.UpcChanged(it))
                    },
                    placeholder = {
                        Text(
                            text = "Input UPC Code",
                            style = InvenceTheme.typography.bodyMedium
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { viewModel.onEvent(ProductFormUiEvent.ScanBarcodeClicked) }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.barcode_scanner),
                                contentDescription = "barcode scan icon"
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                )
            }
            Row(
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(0.2f),
                    text = "Name",
                    style = InvenceTheme.typography.titleSmall
                )
                InvenceOutlineTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.title,
                    onValueChange = {
                        viewModel.onEvent(ProductFormUiEvent.TitleValueChanged(it))
                    },
                    placeholder = {
                        Text(
                            text = "Input Name",
                            style = InvenceTheme.typography.bodyMedium
                        )
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        )
                )
            }
            Row(
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(0.2f),
                    text = "Category",
                    style = InvenceTheme.typography.titleSmall
                )
                when (uiState.selectedCategory) {
                    null -> {
                        InvenceOutlineButton(
                            onClick = {
                                viewModel.onEvent(
                                    ProductFormUiEvent.SelectCategoryClicked
                                )
                            }
                        ) {
                            Text(
                                text = "Select Category",
                                style = InvenceTheme.typography.labelLarge
                            )
                        }
                    }
                    else -> {
                        InvenceSecondaryButton(
                            onClick = {
                                viewModel.onEvent(
                                    ProductFormUiEvent.SelectCategoryClicked
                                )
                            }
                        ) {
                            Text(
                                text = uiState.selectedCategory?.name ?: "",
                                style = InvenceTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
            Row(
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(0.2f),
                    text = "Price",
                    style = InvenceTheme.typography.titleSmall
                )
                InvenceOutlineTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.sellPrice,
                    onValueChange = {
                        viewModel.onEvent(
                            ProductFormUiEvent.SellPriceValueChanged(it)
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Input Price",
                            style = InvenceTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Text(text = "Rp", style = InvenceTheme.typography.bodyMedium)
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                )
            }
            Column {
                Row(
                    modifier =
                        Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Stock", style = InvenceTheme.typography.titleSmall)
                    InvenceOutlineButton(
                        onClick = { viewModel.onEvent(ProductFormUiEvent.AddProductItem) }
                    ) {
                        Text(text = "Add Item", style = InvenceTheme.typography.labelLarge)
                    }
                }
                Divider(modifier = Modifier.padding(top = 8.dp))
                uiState.buyPriceList.forEach { item ->
                    ProductItemCard(
                        itemId = item.key,
                        buyPrice = item.value.price,
                        quantity = item.value.quantity,
                        onBuyPriceChanged = {
                            viewModel.onEvent(
                                ProductFormUiEvent.BuyPriceValueChanged(item.key, it)
                            )
                        },
                        onQuantityChanged = {
                            viewModel.onEvent(
                                ProductFormUiEvent.QuantityValueChanged(item.key, it)
                            )
                        },
                        onRemoveItem = {
                            viewModel.onEvent(
                                ProductFormUiEvent.RemoveProductItem(item.key)
                            )
                        },
                        readOnly = false
                    )
                }
            }
            Row(
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth(0.2f)
                            .padding(top = 8.dp),
                    text = "Note",
                    style = InvenceTheme.typography.titleSmall
                )
                InvenceOutlineTextField(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 120.dp),
                    value = uiState.description,
                    onValueChange = {
                        viewModel.onEvent(
                            ProductFormUiEvent.DescriptionValueChanged(it)
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Input note",
                            style = InvenceTheme.typography.bodyMedium
                        )
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        )
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
        }
    }
}