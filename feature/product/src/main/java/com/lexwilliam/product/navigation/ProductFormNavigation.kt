package com.lexwilliam.product.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lexwilliam.core.navigation.Screen
import com.lexwilliam.product.route.form.ProductFormRoute

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.productFormNavigation(
    onBackStack: () -> Unit,
    toBarcode: (String) -> Unit,
    toInventory: () -> Unit
) {
    composable(
        route = Screen.PRODUCT_FORM.plus("?productUUID={productUUID}&onlyID={onlyID}"),
        arguments =
            listOf(
                navArgument("productUUID") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("onlyID") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
    ) {
        ProductFormRoute(
            onBackStack = onBackStack,
            toBarcode = toBarcode,
            toInventory = toInventory
        )
    }
}

fun NavController.navigateToProductForm(
    productUUID: String? = null,
    onlyID: Boolean = false,
    options: NavOptions? = null
) {
    this.navigate("${Screen.PRODUCT_FORM}?productUUID=$productUUID&onlyID=$onlyID", options)
}