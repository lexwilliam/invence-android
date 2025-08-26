package com.lexwilliam.product.repository

import android.net.Uri
import arrow.core.Either
import com.lexwilliam.product.model.Product
import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.util.DeleteProductFailure
import com.lexwilliam.product.util.UpsertProductFailure
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun observeProductCategory(): Flow<List<ProductCategory>>

    suspend fun upsertProductCategory(
        category: ProductCategory
    ): Either<UpsertProductFailure, ProductCategory>

    suspend fun upsertProduct(
        category: ProductCategory,
        product: Product,
        image: Uri?
    ): Either<UpsertProductFailure, Product>

    suspend fun deleteProductCategory(
        category: ProductCategory
    ): Either<DeleteProductFailure, ProductCategory>

    suspend fun deleteProduct(
        category: ProductCategory,
        product: Product
    ): Either<DeleteProductFailure, Product>
}