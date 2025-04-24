package com.lexwilliam.product.repository

import android.net.Uri
import android.util.Log
import arrow.core.Either
import com.google.firebase.Timestamp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.lexwilliam.core.util.UploadImageFailure
import com.lexwilliam.firebase.utils.FirestoreConfig
import com.lexwilliam.firebase.utils.StorageUploader
import com.lexwilliam.product.model.Product
import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.model.dto.ProductCategoryDto
import com.lexwilliam.product.model.dto.ProductDto
import com.lexwilliam.product.util.DeleteProductFailure
import com.lexwilliam.product.util.UnknownFailure
import com.lexwilliam.product.util.UpsertProductFailure
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import java.util.UUID

internal fun firebaseProductRepository(
    analytics: FirebaseCrashlytics,
    store: FirebaseFirestore,
    storageUploader: StorageUploader,
    functions: FirebaseFunctions,
    json: Json
) = object : ProductRepository {
    override fun observeProductCategory(branchUUID: UUID): Flow<List<ProductCategory>> =
        callbackFlow {
            val reference =
                store
                    .collection(FirestoreConfig.COLLECTION_PRODUCT_CATEGORY)
                    .whereEqualTo(FirestoreConfig.Field.BRANCH_UUID, branchUUID.toString())
                    .whereEqualTo(FirestoreConfig.Field.DELETED_AT, null)

            val registration =
                reference.addSnapshotListener { value, error ->
                    error?.let { exception ->
                        analytics.recordException(exception)
                        trySend(emptyList())
                    }
                    trySend(
                        value
                            ?.toObjects(ProductCategoryDto::class.java)
                            ?.map { productCategory -> productCategory.toDomain() } ?: emptyList()
                    )
                }

            awaitClose { registration.remove() }
        }

    override suspend fun upsertProductCategory(
        category: ProductCategory
    ): Either<UpsertProductFailure, ProductCategory> {
        return Either.catch {
            store
                .collection(FirestoreConfig.COLLECTION_PRODUCT_CATEGORY)
                .document(category.uuid.toString())
                .set(ProductCategoryDto.fromDomain(category))
            category
        }.mapLeft { t ->
            t.printStackTrace()
            analytics.recordException(t)
            UnknownFailure(t.message)
        }
    }

    override suspend fun upsertProduct(
        category: ProductCategory,
        product: Product
    ): Either<UpsertProductFailure, Product> {
        return Either.catch {
            val jsonCategory =
                json
                    .encodeToString(
                        ProductCategoryDto.serializer(),
                        ProductCategoryDto.fromDomain(category)
                    )
            val jsonProduct =
                json
                    .encodeToString(
                        ProductDto.serializer(),
                        ProductDto.fromDomain(product)
                    )
            val data =
                hashMapOf(
                    "category" to jsonCategory,
                    "product" to jsonProduct
                )
            Log.d("TAG", data.toString())
            functions
                .getHttpsCallable("setProduct")
                .call(data)
                .await()
            product
        }.mapLeft { t ->
            t.printStackTrace()
            analytics.recordException(t)
            UnknownFailure(t.message)
        }
    }

    override suspend fun deleteProductCategory(
        category: ProductCategory
    ): Either<DeleteProductFailure, ProductCategory> {
        return Either.catch {
            store
                .collection(FirestoreConfig.COLLECTION_PRODUCT_CATEGORY)
                .document(category.uuid.toString())
                .update(FirestoreConfig.Field.DELETED_AT, Timestamp.now())
            category
        }.mapLeft { t ->
            t.printStackTrace()
            analytics.recordException(t)
            UnknownFailure(t.message)
        }
    }

    override suspend fun deleteProduct(
        category: ProductCategory,
        product: Product
    ): Either<DeleteProductFailure, Product> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadProductCategoryImage(
        branchUUID: UUID,
        categoryUUID: UUID,
        image: Any
    ): Either<UploadImageFailure, Uri> {
        val path = "$branchUUID/category/$categoryUUID"
        return storageUploader.imageUploader(path, image)
    }

    override suspend fun uploadProductImage(
        branchUUID: UUID,
        productUUID: String,
        image: Any
    ): Either<UploadImageFailure, Uri> {
        val path = "$branchUUID/product/$productUUID"
        return storageUploader.imageUploader(path, image)
    }
}