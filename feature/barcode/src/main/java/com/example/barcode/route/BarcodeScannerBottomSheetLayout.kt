package com.example.barcode.route

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
import com.example.barcode.model.BarcodeScannerBottomSheetState
import com.example.barcode.model.InformationModel
import com.lexwilliam.barcode.R
import com.lexwilliam.core_ui.component.button.InvencePrimaryButton
import com.lexwilliam.core_ui.component.button.InvenceSecondaryButton
import com.lexwilliam.core_ui.component.card.ColumnCardWithImage
import com.lexwilliam.core_ui.theme.InvenceTheme

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeScannerBottomSheetLayout(
    resultBottomSheetState: SheetState,
    resultBottomSheetStateModel: BarcodeScannerBottomSheetState,
    viewModel: BarcodeViewModel
) {
    Log.d("TAG", resultBottomSheetStateModel.toString())
    ModalBottomSheet(
        sheetState = resultBottomSheetState,
        content = {
            when (resultBottomSheetStateModel) {
                is BarcodeScannerBottomSheetState.Loading -> {
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
                is BarcodeScannerBottomSheetState.ProductFound -> {
                    Log.d("TAG", "ProductFound")
                    BarcodeScannerInformationLayout(
                        barCodeResult = resultBottomSheetStateModel.barcodeResult,
                        informationModel = resultBottomSheetStateModel.information,
                        viewModel = viewModel
                    )
                }
                is BarcodeScannerBottomSheetState.AddProduct -> {
                    Log.d("TAG", "AddProduct")
                    BarcodeScannerAddProductLayout(
                        barCodeResult = resultBottomSheetStateModel.barcodeResult,
                        viewModel = viewModel
                    )
                }
                is BarcodeScannerBottomSheetState.OnlyID -> {
                    BarcodeScannerOnlyIDLayout(
                        barCodeResult = resultBottomSheetStateModel.barcodeResult,
                        viewModel = viewModel
                    )
                }
                is BarcodeScannerBottomSheetState.Error -> {
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
                is BarcodeScannerBottomSheetState.Hidden -> {
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
        onDismissRequest = {}
    )
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun BarcodeScannerInformationLayout(
    barCodeResult: BarCodeResult,
    informationModel: InformationModel,
    viewModel: BarcodeViewModel
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
            onClick = { viewModel.handleToProductDetailClicked() }
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
    viewModel: BarcodeViewModel
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
                viewModel.onBackButtonClicked()
            }) {
                Text(text = "No, cancel it", style = InvenceTheme.typography.labelLarge)
            }
            InvencePrimaryButton(modifier = Modifier.weight(1f), onClick = {
                viewModel.handleAddProductClicked()
            }) {
                Text(text = "Yes, add it", style = InvenceTheme.typography.labelLarge)
            }
        }
        Spacer(modifier = Modifier.size(20.dp))
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun BarcodeScannerOnlyIDLayout(
    barCodeResult: BarCodeResult,
    viewModel: BarcodeViewModel
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
        InvencePrimaryButton(modifier = Modifier.fillMaxWidth(), onClick = {
            viewModel.handleOnlyIDConfirm()
        }) {
            Text(text = "Confirm", style = InvenceTheme.typography.labelLarge)
        }
        Spacer(modifier = Modifier.size(20.dp))
    }
}