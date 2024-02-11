package com.example.barcode.navigation

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.barcode.route.BarcodeRoute
import com.lexwilliam.core.navigation.Screen

@OptIn(ExperimentalGetImage::class)
fun NavGraphBuilder.barcodeNavigation(
    onBackStack: () -> Unit,
    toProductForm: (String?, Boolean) -> Unit,
    toProductDetail: (String) -> Unit
) {
    composable(
        route = "${Screen.BARCODE}?onlyID={onlyID}",
        arguments =
            listOf(
                navArgument("onlyID") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
    ) {
        BarcodeRoute(
            onBackStack = onBackStack,
            toProductForm = toProductForm,
            toProductDetail = toProductDetail
        )
    }
}

fun NavController.navigateToBarcode(
    onlyID: String? = null,
    options: NavOptions? = null
) {
    this.navigate("${Screen.BARCODE}?onlyID=$onlyID", options)
}