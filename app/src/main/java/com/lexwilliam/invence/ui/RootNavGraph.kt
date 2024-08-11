package com.lexwilliam.invence.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lexwilliam.auth.navigation.loginNavigation
import com.lexwilliam.auth.navigation.navigateToLogin
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
import com.lexwilliam.profile.navigation.navigateToProfile
import com.lexwilliam.profile.navigation.profileNavigation
import com.lexwilliam.transaction.detail.navigation.navigateToTransactionDetail
import com.lexwilliam.transaction.detail.navigation.transactionDetailNavigation
import com.lexwilliam.transaction.history.navigation.navigateToTransactionHistory
import com.lexwilliam.transaction.history.navigation.transactionHistoryNavigation

@Composable
fun RootNavGraph(
    startDestination: String = Screen.LOGIN,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        loginNavigation(
            toCompanySearch = navController::navigateToCompanySearch,
            toHome = navController::navigateToHome
        )
        homeNavigation(
            toInventory = navController::navigateToInventory,
            toCart = navController::navigateToCart,
            toTransactionHistory = navController::navigateToTransactionHistory,
            toTransactionDetail = { transactionUUID ->
                navController.navigateToTransactionDetail(transactionUUID)
            },
            toProfile = navController::navigateToProfile
        )
        inventoryNavigation(
            toProductForm = { productUUID ->
                navController.navigateToProductForm(productUUID)
            },
            toProductDetail = { productUUID -> navController.navigateToProductDetail(productUUID) }
        )
        productFormNavigation(
            onBackStack = navController::navigateUp
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
        profileNavigation(
            onBackStack = navController::navigateUp,
            toLogin = navController::navigateToLogin
        )
        composable(route = Screen.SPLASH) {}
    }
}