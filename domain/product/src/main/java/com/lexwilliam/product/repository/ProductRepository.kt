package com.lexwilliam.product.repository

import arrow.core.Either
import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.util.DeleteProductFailure
import com.lexwilliam.product.util.UpsertProductFailure
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ProductRepository {
    fun observeProductCategory(branchUUID: UUID): Flow<List<ProductCategory>>

    suspend fun upsertProductCategory(
        category: ProductCategory
    ): Either<UpsertProductFailure, ProductCategory>

    suspend fun deleteProductCategory(
        category: ProductCategory
    ): Either<DeleteProductFailure, ProductCategory>
}