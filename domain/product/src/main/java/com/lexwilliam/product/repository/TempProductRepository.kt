package com.lexwilliam.product.repository

import com.lexwilliam.product.model.ProductWithImageFormat
import kotlinx.coroutines.flow.Flow

interface TempProductRepository {
    val productFlow: Flow<ProductWithImageFormat?>

    suspend fun insertTempProduct(product: ProductWithImageFormat): Boolean

    suspend fun clearTempProduct(): Boolean
}