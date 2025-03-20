package com.lexwilliam.invence.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lexwilliam.analytics.navigation.analyticsNavigation
import com.lexwilliam.analytics.navigation.navigateToAnalytics
import com.lexwilliam.auth.route.forgot.navigation.forgotNavigation
import com.lexwilliam.auth.route.forgot.navigation.navigateToForgotPassword
import com.lexwilliam.auth.route.login.navigation.loginNavigation
import com.lexwilliam.auth.route.login.navigation.navigateToLogin
import com.lexwilliam.auth.route.signup.navigation.navigateToSignUp
import com.lexwilliam.auth.route.signup.navigation.signUpNavigation
import com.lexwilliam.category.navigation.categoryNavigation
import com.lexwilliam.category.navigation.navigateToCategory
import com.lexwilliam.company.navigation.companyFormNavigation
import com.lexwilliam.company.navigation.companySearchNavigation
import com.lexwilliam.company.navigation.navigateToCompanyForm
import com.lexwilliam.company.navigation.navigateToCompanySearch
import com.lexwilliam.core.navigation.Screen
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
            toCompanySearch = {
                navController.navigateToCompanySearch(
                    options =
                        NavOptions.Builder()
                            .setPopUpTo(Screen.LOGIN, true)
                            .build()
                )
            },
            toHome = {
                navController.navigateToInventory(
                    options =
                        NavOptions.Builder()
                            .setPopUpTo(Screen.LOGIN, true)
                            .build()
                )
            },
            toForgotPassword = navController::navigateToForgotPassword,
            toSignUp = navController::navigateToSignUp
        )
        signUpNavigation(
            onBackStack = navController::navigateUp,
            toCompanySearch = navController::navigateToCompanySearch
        )
        forgotNavigation(
            onBackStack = navController::navigateUp
        )
        inventoryNavigation(
            toProductForm = { productUUID ->
                navController.navigateToProductForm(productUUID)
            },
            toProductDetail = { productUUID -> navController.navigateToProductDetail(productUUID) },
            toCategory = navController::navigateToCategory,
            onDrawerNavigation = { screen -> onDrawerNavigation(navController, screen) }
        )
        categoryNavigation(
            onBackStack = navController::navigateUp
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
            toHome = navController::navigateToInventory
        )
        companyFormNavigation(
            toHome = navController::navigateToInventory
        )
        cartNavigation(
            toOrder = { orderUUID -> navController.navigateToOrder(orderUUID) },
            onDrawerNavigation = { screen -> onDrawerNavigation(navController, screen) }
        )
        orderNavigation(
            onBackStack = navController::navigateUp,
            toCheckOut = { orderUUID -> navController.navigateToCheckOut(orderUUID) }
        )
        checkOutNavigation(
            onBackStack = navController::navigateUp,
            toCart = {
                navController.navigateToCart(
                    options =
                        NavOptions.Builder()
                            .setPopUpTo(Screen.CART, true)
                            .build()
                )
            },
            toTransactionDetail = { transactionUUID ->
                navController.navigateToTransactionDetail(
                    transactionUUID = transactionUUID,
                    options =
                        NavOptions.Builder()
                            .setPopUpTo(Screen.CART, true)
                            .build()
                )
            }
        )
        transactionDetailNavigation(
            onBackStack = navController::navigateUp
        )
        transactionHistoryNavigation(
            onDrawerNavigation = { screen -> onDrawerNavigation(navController, screen) }
        )
        profileNavigation(
            toLogin = navController::navigateToLogin,
            onDrawerNavigation = { screen -> onDrawerNavigation(navController, screen) }
        )
        analyticsNavigation(
            onDrawerNavigation = { screen -> onDrawerNavigation(navController, screen) }
        )
        composable(route = Screen.SPLASH) {}
    }
}

fun onDrawerNavigation(
    navController: NavHostController,
    screen: String
) {
    when (screen) {
        Screen.INVENTORY -> navController.navigateToInventory()
        Screen.CART -> navController.navigateToCart()
        Screen.ANALYTICS -> navController.navigateToAnalytics()
        Screen.TRANSACTION_HISTORY -> navController.navigateToTransactionHistory()
        Screen.PROFILE -> navController.navigateToProfile()
    }
}