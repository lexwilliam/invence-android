package com.lexwilliam.product.repository

import android.graphics.Bitmap
import android.net.Uri
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.google.firebase.Timestamp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.lexwilliam.core.model.UploadImageFormat
import com.lexwilliam.firebase.FirestoreConfig
import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.model.dto.ProductCategoryDto
import com.lexwilliam.product.util.DeleteProductFailure
import com.lexwilliam.product.util.UnknownFailure
import com.lexwilliam.product.util.UploadImageFailure
import com.lexwilliam.product.util.UpsertProductFailure
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID

internal fun firebaseProductRepository(
    analytics: FirebaseCrashlytics,
    store: FirebaseFirestore,
    storage: FirebaseStorage
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

    override suspend fun uploadProductCategoryImage(
        branchUUID: UUID,
        categoryUUID: UUID,
        format: UploadImageFormat
    ): Either<UploadImageFailure, Uri> {
        return Either.catch {
            val imageRef =
                storage
                    .reference
                    .child("category/$branchUUID/$categoryUUID.jpg}")
            when (format) {
                is UploadImageFormat.WithUri -> {
                    imageRef.putFile(format.uri)
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                        }
                    val uploadTask = imageRef.putFile(format.uri)
                    val urlTask =
                        uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let {
                                    throw it
                                }
                            }
                            imageRef.downloadUrl
                        }.await()
                    return urlTask?.right()
                        ?: UploadImageFailure.UploadFailure("Task Failed").left()
                }
                is UploadImageFormat.WithBitmap -> {
                    val baos = ByteArrayOutputStream()
                    format.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    val uploadTask = imageRef.putBytes(data)
                    val urlTask =
                        uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let {
                                    throw it
                                }
                            }
                            imageRef.downloadUrl
                        }.await()
                    return urlTask?.right()
                        ?: UploadImageFailure.UploadFailure("Task Failed").left()
                }
            }
        }.mapLeft { t ->
            t.printStackTrace()
            analytics.recordException(t)
            UnknownFailure(t.message)
        }
    }

    override suspend fun uploadProductImage(
        branchUUID: UUID,
        productUUID: String,
        format: UploadImageFormat
    ): Either<UploadImageFailure, Uri> {
        return Either.catch {
            val imageRef =
                storage
                    .reference
                    .child("product/$branchUUID/$productUUID.jpg}")
            when (format) {
                is UploadImageFormat.WithUri -> {
                    imageRef.putFile(format.uri)
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                        }
                    val uploadTask = imageRef.putFile(format.uri)
                    val urlTask =
                        uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let {
                                    throw it
                                }
                            }
                            imageRef.downloadUrl
                        }.await()
                    return urlTask?.right()
                        ?: UploadImageFailure.UploadFailure("Task Failed").left()
                }
                is UploadImageFormat.WithBitmap -> {
                    val baos = ByteArrayOutputStream()
                    format.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    val uploadTask = imageRef.putBytes(data)
                    val urlTask =
                        uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let {
                                    throw it
                                }
                            }
                            imageRef.downloadUrl
                        }.await()
                    return urlTask?.right()
                        ?: UploadImageFailure.UploadFailure("Task Failed").left()
                }
            }
        }.mapLeft { t ->
            t.printStackTrace()
            analytics.recordException(t)
            UnknownFailure(t.message)
        }
    }
}