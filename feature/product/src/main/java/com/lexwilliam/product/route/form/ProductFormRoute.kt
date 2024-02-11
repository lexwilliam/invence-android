package com.lexwilliam.product.route.form

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lexwilliam.category.dialog.SelectCategoryDialog
import com.lexwilliam.core_ui.R
import com.lexwilliam.core_ui.component.ObserveAsEvents
import com.lexwilliam.core_ui.component.button.InvencePrimaryButton
import com.lexwilliam.core_ui.component.button.InvenceSecondaryButton
import com.lexwilliam.core_ui.component.camera.GetPhotoCameraPreview
import com.lexwilliam.core_ui.component.image.InputImage
import com.lexwilliam.core_ui.component.textfield.InvenceOutlineTextField
import com.lexwilliam.core_ui.component.topbar.InvenceCenterAlignedTopBar
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.product.category.CategoryUiEvent
import com.lexwilliam.product.component.ProductItemCard
import com.lexwilliam.product.navigation.ProductFormNavigationTarget

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormRoute(
    viewModel: ProductFormViewModel = hiltViewModel(),
    onBackStack: () -> Unit,
    toBarcode: (String) -> Unit,
    toInventory: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val productUUID by viewModel.productUUID.collectAsStateWithLifecycle()
    val product by viewModel.product.collectAsStateWithLifecycle()
    val categoryState by viewModel.categoryState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.navigation) { target ->
        when (target) {
            is ProductFormNavigationTarget.BackStack -> onBackStack()
            is ProductFormNavigationTarget.Barcode -> toBarcode(target.onlyID)
            is ProductFormNavigationTarget.Inventory -> toInventory()
        }
    }

    categoryState?.let { state ->
        SelectCategoryDialog(
            onDismiss = { viewModel.onCategoryDialogEvent(CategoryUiEvent.Dismiss) },
            categories = state.categories,
            query = state.query,
            onQueryChanged = { viewModel.onCategoryDialogEvent(CategoryUiEvent.QueryChanged(it)) },
            onCategoryClicked = {
                viewModel.onCategoryDialogEvent(
                    CategoryUiEvent.CategoryClicked(it)
                )
            },
            formImagePath = state.formImagePath,
            formInputImageChanged = {
                viewModel.onCategoryDialogEvent(
                    CategoryUiEvent.InputImageChanged(it)
                )
            },
            isFormShown = state.isFormShown,
            showForm = { viewModel.onCategoryDialogEvent(CategoryUiEvent.ShowForm(it)) },
            formTitle = state.formTitle,
            formTitleChanged = {
                viewModel.onCategoryDialogEvent(
                    CategoryUiEvent.AddCategoryTitleChanged(it)
                )
            },
            formOnConfirm = { viewModel.onCategoryDialogEvent(CategoryUiEvent.AddCategoryConfirm) }
        )
    }

    if (uiState.takePhoto) {
        GetPhotoCameraPreview(
            onPhotoTaken = { viewModel.onEvent(ProductFormUiEvent.ProductPhotoTaken(it)) }
        )
    } else {
        Scaffold(
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
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 16.dp
                                ),
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
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InputImage(
                    imageModifier =
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                    imagePath = uiState.image,
                    label = "Upload Product Image",
                    onImageChanged = { uri ->
                        viewModel.onEvent(ProductFormUiEvent.InputImageChanged(uri))
                    },
                    onCameraClicked = {
                        viewModel.onEvent(ProductFormUiEvent.CameraClicked)
                    },
                    enableCamera = true
                )
                if (productUUID == null) {
                    InvencePrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.onEvent(ProductFormUiEvent.ScanBarcodeClicked) }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.barcode_scanner),
                            contentDescription = "barcode scan icon",
                            tint = InvenceTheme.colors.neutral10
                        )
                        Text(
                            modifier = Modifier.padding(start = 16.dp),
                            text = "Scan Barcode",
                            style = InvenceTheme.typography.labelLarge
                        )
                    }
                } else {
                    InvenceSecondaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.onEvent(ProductFormUiEvent.ScanBarcodeClicked) }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.barcode_scanner),
                            contentDescription = "barcode scan icon"
                        )
                        Text(
                            modifier = Modifier.padding(start = 16.dp),
                            text = "ID: $productUUID",
                            style = InvenceTheme.typography.labelLarge
                        )
                    }
                }
                Column {
                    Text(
                        text = "Title",
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
                                text = "Input Title",
                                style = InvenceTheme.typography.bodyLarge
                            )
                        }
                    )
                }
                Column {
                    Text(
                        text = "Sell Price",
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
                                text = "Input Sell Price",
                                style = InvenceTheme.typography.bodyLarge
                            )
                        }
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
                            onClick = { viewModel.onEvent(ProductFormUiEvent.AddProductItem) }
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "add product item icon"
                            )
                        }
                    }
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
                Column {
                    Text(
                        text = "Category",
                        style = InvenceTheme.typography.titleSmall
                    )
                    Column {
                        InvenceOutlineTextField(
                            modifier =
                                Modifier
                                    .fillMaxWidth(),
                            value = uiState.selectedCategory?.name ?: "",
                            onValueChange = { },
                            placeholder = {
                                Text(
                                    text = "Select Category",
                                    style = InvenceTheme.typography.bodyLarge
                                )
                            },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "pick category icon"
                                )
                            },
                            interactionSource =
                                remember { MutableInteractionSource() }
                                    .also { interactionSource ->
                                        LaunchedEffect(interactionSource) {
                                            interactionSource.interactions.collect {
                                                if (it is PressInteraction.Release) {
                                                    viewModel.onEvent(
                                                        ProductFormUiEvent.SelectCategoryClicked
                                                    )
                                                }
                                            }
                                        }
                                    }
                        )
                    }
                }
                Column {
                    Text(
                        text = "Description",
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
                                text = "Input Description",
                                style = InvenceTheme.typography.bodyLarge
                            )
                        }
                    )
                }
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
    }
}