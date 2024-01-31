package com.lexwilliam.product.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.product.route.form.ProductFormRoute
import java.util.UUID

fun NavGraphBuilder.productFormNavigation(onBackStack: () -> Unit) {
    composable(
        route = Screen.PRODUCT_FORM.plus("?productUUID={productUUID}"),
        arguments =
            listOf(
                navArgument("productUUID") {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                }
            )
    ) {
        ProductFormRoute(
            onBackStack = onBackStack
        )
    }
}

fun NavController.navigateToProductForm(
    productUUID: UUID?,
    options: NavOptions? = null
) {
    this.navigate("${Screen.PRODUCT_FORM}?productUUID=$productUUID", options)
}