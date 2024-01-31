package com.lexwilliam.product.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.product.route.detail.ProductDetailRoute
import java.util.UUID

fun NavGraphBuilder.productDetailNavigation(onBackStack: () -> Unit) {
    composable(
        route = Screen.PRODUCT_DETAIL.plus("?productUUID={productUUID}"),
        arguments =
            listOf(
                navArgument("productUUID") {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                }
            )
    ) {
        ProductDetailRoute(
            onBackStack = onBackStack
        )
    }
}

fun NavController.navigateToProductDetail(
    productUUID: UUID,
    options: NavOptions? = null
) {
    this.navigate("${Screen.PRODUCT_DETAIL}?productUUID=$productUUID", options)
}