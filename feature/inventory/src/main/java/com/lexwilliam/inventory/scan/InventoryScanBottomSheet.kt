package com.lexwilliam.inventory.scan

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.barcode.model.BarCodeResult
import com.example.barcode.model.InformationModel
import com.lexwilliam.barcode.R
import com.lexwilliam.core_ui.component.button.InvencePrimaryButton
import com.lexwilliam.core_ui.component.button.InvenceSecondaryButton
import com.lexwilliam.core_ui.component.card.ColumnCardWithImage
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.inventory.route.InventoryViewModel

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScanBottomSheet(
    resultBottomSheetState: SheetState,
    resultBottomSheetStateModel: InventoryScanBottomSheetState,
    viewModel: InventoryViewModel
) {
    ModalBottomSheet(
        sheetState = resultBottomSheetState,
        content = {
            when (resultBottomSheetStateModel) {
                is InventoryScanBottomSheetState.Loading -> {
                    Log.d("TAG", "Loading")
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Loading...", style = InvenceTheme.typography.bodyLarge)
//                        CircularProgressIndicator(
//                            color = InvenceTheme.colors.primary
//                        )
                    }
                }
                is InventoryScanBottomSheetState.ProductFound -> {
                    BarcodeScannerInformationLayout(
                        barCodeResult = resultBottomSheetStateModel.barcodeResult,
                        informationModel = resultBottomSheetStateModel.information,
                        onProductDetailClicked = {
                            viewModel.onScanEvent(
                                ScanEvent.ProductDetailClicked
                            )
                        }
                    )
                }
                is InventoryScanBottomSheetState.AddProduct -> {
                    BarcodeScannerAddProductLayout(
                        barCodeResult = resultBottomSheetStateModel.barcodeResult,
                        onBackClicked = { viewModel.onScanEvent(ScanEvent.BottomSheetDismiss) },
                        onAddProductClicked = { viewModel.onScanEvent(ScanEvent.AddProductClicked) }
                    )
                }
                is InventoryScanBottomSheetState.Error -> {
                    Log.d("TAG", "Error")
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text =
                                    stringResource(
                                        id = R.string.loading_product_result_error_generic
                                    ),
                                style = InvenceTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                is InventoryScanBottomSheetState.Hidden -> {
                    Log.d("TAG", "Hidden")
                    // bottom sheet content should have real content at any case. otherwise it will throw exception
                    Text(text = "")
                }
            }
        },
        modifier =
            Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        onDismissRequest = { viewModel.onScanEvent(ScanEvent.BottomSheetDismiss) }
    )
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun BarcodeScannerInformationLayout(
    barCodeResult: BarCodeResult,
    informationModel: InformationModel,
    onProductDetailClicked: () -> Unit
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            modifier = Modifier,
            text = "ID: ${barCodeResult.barCode.displayValue}",
            textAlign = TextAlign.Center,
            style = InvenceTheme.typography.bodyLarge
        )
        ColumnCardWithImage(
            modifier = Modifier.fillMaxWidth(),
            imagePath = informationModel.product?.imagePath
        ) {
            Text(
                text = informationModel.product?.name.toString(),
                style = InvenceTheme.typography.bodyMedium
            )
        }
        InvencePrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onProductDetailClicked
        ) {
            Text(text = "Go to Product Detail", style = InvenceTheme.typography.labelLarge)
        }
        Spacer(modifier = Modifier.size(20.dp))
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun BarcodeScannerAddProductLayout(
    barCodeResult: BarCodeResult,
    onBackClicked: () -> Unit,
    onAddProductClicked: () -> Unit
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            modifier = Modifier,
            text = "ID: ${barCodeResult.barCode.displayValue}",
            textAlign = TextAlign.Center,
            style = InvenceTheme.typography.bodyLarge
        )
        Text(
            text = "We have not found this product, do you want to add it?",
            style = InvenceTheme.typography.bodyMedium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InvenceSecondaryButton(modifier = Modifier.weight(1f), onClick = {
                onBackClicked()
            }) {
                Text(text = "No, cancel it", style = InvenceTheme.typography.labelLarge)
            }
            InvencePrimaryButton(modifier = Modifier.weight(1f), onClick = {
                onAddProductClicked()
            }) {
                Text(text = "Yes, add it", style = InvenceTheme.typography.labelLarge)
            }
        }
        Spacer(modifier = Modifier.size(20.dp))
    }
}