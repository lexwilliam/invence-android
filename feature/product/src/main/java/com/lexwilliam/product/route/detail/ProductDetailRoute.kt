package com.lexwilliam.product.route.detail

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProductDetailRoute(
    viewModel: ProductDetailViewModel = hiltViewModel(),
    onBackStack: () -> Unit
) {
}