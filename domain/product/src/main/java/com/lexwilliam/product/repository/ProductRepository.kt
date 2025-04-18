package com.lexwilliam.product.repository

import android.net.Uri
import arrow.core.Either
import com.lexwilliam.core.util.UploadImageFailure
import com.lexwilliam.product.model.Product
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

    suspend fun upsertProduct(
        category: ProductCategory,
        product: Product
    ): Either<UpsertProductFailure, Product>

    suspend fun deleteProductCategory(
        category: ProductCategory
    ): Either<DeleteProductFailure, ProductCategory>

    suspend fun deleteProduct(
        category: ProductCategory,
        product: Product
    ): Either<DeleteProductFailure, Product>

    suspend fun uploadProductCategoryImage(
        branchUUID: UUID,
        categoryUUID: UUID,
        image: Any
    ): Either<UploadImageFailure, Uri>

    suspend fun uploadProductImage(
        branchUUID: UUID,
        productUUID: String,
        image: Any
    ): Either<UploadImageFailure, Uri>
}