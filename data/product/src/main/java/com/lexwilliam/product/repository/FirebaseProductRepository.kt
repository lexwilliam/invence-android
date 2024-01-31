package com.lexwilliam.product.repository

import arrow.core.Either
import com.google.firebase.Timestamp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.lexwilliam.firebase.FirestoreConfig
import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.model.dto.ProductCategoryDto
import com.lexwilliam.product.util.DeleteProductFailure
import com.lexwilliam.product.util.UnknownFailure
import com.lexwilliam.product.util.UpsertProductFailure
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

internal fun firebaseProductRepository(
    analytics: FirebaseCrashlytics,
    store: FirebaseFirestore
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
}