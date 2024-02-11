package com.lexwilliam.product.repository

import com.lexwilliam.product.model.ProductWithImageFormat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

fun inMemoryTempProductRepository() =
    object : TempProductRepository {
        private val _product = MutableStateFlow<ProductWithImageFormat?>(null)
        override val productFlow: Flow<ProductWithImageFormat?> = _product.asStateFlow()

        override suspend fun insertTempProduct(product: ProductWithImageFormat): Boolean {
            _product.update { product }

            return true
        }

        override suspend fun clearTempProduct(): Boolean {
            _product.update { null }
            return true
        }
    }