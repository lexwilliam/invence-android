package com.lexwilliam.product.repository

import android.net.Uri
import arrow.core.Either
import com.google.firebase.Timestamp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.lexwilliam.core.session.ObserveSessionUseCase
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json

internal fun firebaseProductRepository(
    observeSession: ObserveSessionUseCase,
    analytics: FirebaseCrashlytics,
    store: FirebaseFirestore,
    storageUploader: StorageUploader,
    functions: FirebaseFunctions,
    json: Json
) = object : ProductRepository {
    val userUUID: Flow<String?> = observeSession().map { it.getUserId() }

    override fun observeProductCategory(): Flow<List<ProductCategory>> =
        callbackFlow {
            val userUUID = userUUID.firstOrNull()
            if (userUUID == null) trySend(emptyList())
            val reference =
                store
                    .collection(FirestoreConfig.COLLECTION_PRODUCT_CATEGORY)
                    .whereEqualTo(FirestoreConfig.Field.USER_UUID, userUUID.toString())
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
        product: Product,
        image: Uri?
    ): Either<UpsertProductFailure, Product> {
        return Either.catch {
            var imageUrl: Uri? = null

            if (image != null) {
                val uriSource = getUriSource(image)
                if (uriSource == "file" || uriSource == "content") {
                    when (
                        val result =
                            uploadProductImage(
                                product.sku,
                                image
                            )
                    ) {
                        is Either.Left -> {
                            return Either.Left(UpsertProductFailure.UploadImageFailed)
                        }

                        is Either.Right -> {
                            imageUrl = result.value
                        }
                    }
                } else {
                    imageUrl = image
                }
            }

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
                        ProductDto.fromDomain(product.copy(imagePath = imageUrl))
                    )
            val data =
                hashMapOf(
                    "category" to jsonCategory,
                    "product" to jsonProduct
                )
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

    suspend fun uploadProductImage(
        productUUID: String,
        image: Uri
    ): Either<UploadImageFailure, Uri> {
        val userUUID = userUUID.firstOrNull()
        if (userUUID == null) return Either.Left(UploadImageFailure.Unauthenticated)
        val path = "$userUUID/product/$productUUID"
        return storageUploader.imageUploader(path, image)
    }

    fun getUriSource(uri: Uri): String {
        return when (uri.scheme) {
            "file" -> "Internal or External Storage (Direct File Path)"
            "content" -> "Internal or External Storage (via ContentProvider)"
            "http", "https" -> "Network"
            else -> "Unknown Source"
        }
    }
}