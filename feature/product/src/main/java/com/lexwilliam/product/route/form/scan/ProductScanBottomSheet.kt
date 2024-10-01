package com.lexwilliam.product.route.form.scan

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
import com.lexwilliam.barcode.R
import com.lexwilliam.core_ui.component.button.InvenceOutlineButton
import com.lexwilliam.core_ui.component.button.InvencePrimaryButton
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.inventory.scan.ProductScanEvent
import com.lexwilliam.product.route.form.ProductFormViewModel

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScanBottomSheet(
    resultBottomSheetState: SheetState,
    resultBottomSheetStateModel: ProductScanBottomSheetState,
    viewModel: ProductFormViewModel
) {
    ModalBottomSheet(
        containerColor = InvenceTheme.colors.neutral10,
        sheetState = resultBottomSheetState,
        content = {
            when (resultBottomSheetStateModel) {
                is ProductScanBottomSheetState.Loading -> {
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
                is ProductScanBottomSheetState.ScanResult -> {
                    BarcodeScannerResultLayout(
                        barCodeResult = resultBottomSheetStateModel.barcodeResult,
                        onConfirmClicked = {
                            viewModel.onScanEvent(
                                ProductScanEvent.ConfirmClicked
                            )
                        },
                        onCancelClicked = {
                            viewModel.onScanEvent(
                                ProductScanEvent.BottomSheetDismiss
                            )
                        }
                    )
                }
                is ProductScanBottomSheetState.Error -> {
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
                is ProductScanBottomSheetState.Hidden -> {
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
        onDismissRequest = { viewModel.onScanEvent(ProductScanEvent.BottomSheetDismiss) }
    )
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun BarcodeScannerResultLayout(
    barCodeResult: BarCodeResult,
    onCancelClicked: () -> Unit,
    onConfirmClicked: () -> Unit
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InvenceOutlineButton(modifier = Modifier.weight(1f), onClick = {
                onCancelClicked()
            }) {
                Text(text = "No, cancel it", style = InvenceTheme.typography.labelLarge)
            }
            InvencePrimaryButton(modifier = Modifier.weight(1f), onClick = {
                onConfirmClicked()
            }) {
                Text(text = "Yes, add it", style = InvenceTheme.typography.labelLarge)
            }
        }
        Spacer(modifier = Modifier.size(20.dp))
    }
}