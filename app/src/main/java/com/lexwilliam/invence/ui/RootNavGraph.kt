package com.lexwilliam.invence.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.barcode.navigation.barcodeNavigation
import com.example.barcode.navigation.navigateToBarcode
import com.lexwilliam.auth.navigation.loginNavigation
import com.lexwilliam.auth.util.GoogleAuthUiClient
import com.lexwilliam.company.navigation.companyFormNavigation
import com.lexwilliam.company.navigation.companySearchNavigation
import com.lexwilliam.company.navigation.navigateToCompanyForm
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.inventory.navigation.inventoryNavigation
import com.lexwilliam.inventory.navigation.navigateToInventory
import com.lexwilliam.product.navigation.navigateToProductDetail
import com.lexwilliam.product.navigation.navigateToProductForm
import com.lexwilliam.product.navigation.productDetailNavigation
import com.lexwilliam.product.navigation.productFormNavigation

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RootNavGraph(
    startDestination: String = Screen.SPLASH,
    navController: NavHostController,
    lifecycleScope: LifecycleCoroutineScope,
    googleAuthUiClient: GoogleAuthUiClient
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        loginNavigation(
            lifecycleScope = lifecycleScope,
            googleAuthUiClient = googleAuthUiClient,
            toInventory = navController::navigateToInventory
        )
        inventoryNavigation(
            toProductForm = { productUUID ->
                navController.navigateToProductForm(productUUID)
            },
            toProductDetail = { productUUID -> navController.navigateToProductDetail(productUUID) },
            toBarcode = { navController.navigateToBarcode() }
        )
        productFormNavigation(
            onBackStack = navController::navigateUp,
            toBarcode = { onlyID -> navController.navigateToBarcode(onlyID) },
            toInventory = navController::navigateToInventory
        )
        productDetailNavigation(
            onBackStack = navController::navigateUp,
            toProductForm = { productUUID ->
                navController.navigateToProductForm(productUUID)
            }
        )
        companySearchNavigation(
            toCompanyForm = navController::navigateToCompanyForm
        )
        companyFormNavigation(
            toInventory = navController::navigateToInventory
        )
        barcodeNavigation(
            onBackStack = navController::navigateUp,
            toProductDetail = { productUUID ->
                navController.navigateToProductDetail(
                    productUUID,
                    NavOptions.Builder()
                        .setPopUpTo(Screen.BARCODE, true)
                        .build()
                )
            },
            toProductForm = { productUUID, onlyID ->
                navController.navigateToProductForm(
                    productUUID,
                    onlyID,
                    NavOptions.Builder()
                        .setPopUpTo(Screen.BARCODE, true)
                        .build()
                )
            }
        )
        composable(route = Screen.SPLASH) {}
    }
}