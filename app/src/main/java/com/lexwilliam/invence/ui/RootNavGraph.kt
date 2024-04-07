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
import com.lexwilliam.company.navigation.navigateToCompanySearch
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.home.navigation.homeNavigation
import com.lexwilliam.home.navigation.navigateToHome
import com.lexwilliam.inventory.navigation.inventoryNavigation
import com.lexwilliam.inventory.navigation.navigateToInventory
import com.lexwilliam.order.cart.navigation.cartNavigation
import com.lexwilliam.order.cart.navigation.navigateToCart
import com.lexwilliam.order.checkout.navigation.checkOutNavigation
import com.lexwilliam.order.checkout.navigation.navigateToCheckOut
import com.lexwilliam.order.order.navigation.navigateToOrder
import com.lexwilliam.order.order.navigation.orderNavigation
import com.lexwilliam.product.navigation.navigateToProductDetail
import com.lexwilliam.product.navigation.navigateToProductForm
import com.lexwilliam.product.navigation.productDetailNavigation
import com.lexwilliam.product.navigation.productFormNavigation
import com.lexwilliam.transaction.detail.navigation.navigateToTransactionDetail
import com.lexwilliam.transaction.detail.navigation.transactionDetailNavigation
import com.lexwilliam.transaction.history.navigation.navigateToTransactionHistory
import com.lexwilliam.transaction.history.navigation.transactionHistoryNavigation

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RootNavGraph(
    startDestination: String = Screen.LOGIN,
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
            toCompanySearch = navController::navigateToCompanySearch
        )
        homeNavigation(
            toInventory = navController::navigateToInventory,
            toCart = navController::navigateToCart,
            toTransactionHistory = navController::navigateToTransactionHistory,
            toTransactionDetail = { transactionUUID ->
                navController.navigateToTransactionDetail(transactionUUID)
            }
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
            toCompanyForm = navController::navigateToCompanyForm,
            toHome = navController::navigateToHome
        )
        companyFormNavigation(
            toHome = navController::navigateToHome
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
        cartNavigation(
            toOrder = { orderUUID -> navController.navigateToOrder(orderUUID) }
        )
        orderNavigation(
            toCheckOut = { orderUUID -> navController.navigateToCheckOut(orderUUID) }
        )
        checkOutNavigation(
            onBackStack = navController::navigateUp,
            toCart = navController::navigateToCart,
            toTransactionDetail = { transactionUUID ->
                navController.navigateToTransactionDetail(
                    transactionUUID = transactionUUID,
                    options =
                        NavOptions.Builder()
                            .setPopUpTo(Screen.ORDER, true)
                            .build()
                )
            }
        )
        transactionDetailNavigation(
            onBackStack = navController::navigateUp
        )
        transactionHistoryNavigation(
            onBackStack = navController::navigateUp
        )
        composable(route = Screen.SPLASH) {}
    }
}