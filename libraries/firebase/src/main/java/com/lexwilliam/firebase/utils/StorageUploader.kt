package com.lexwilliam.firebase.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.storage.FirebaseStorage
import com.lexwilliam.core.util.UploadImageFailure
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class StorageUploader
    @Inject
    constructor(
        private val analytics: FirebaseCrashlytics,
        private val storage: FirebaseStorage,
        private val context: Context
    ) {
        suspend fun imageUploader(
            path: String,
            image: Any
        ): Either<UploadImageFailure, Uri> {
            return Either.catch {
                val imageRef =
                    storage
                        .reference
                        .child(path)
                val uploadTask =
                    when (image) {
                        is Bitmap -> {
                            val baos = ByteArrayOutputStream()
                            image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                            imageRef.putBytes(baos.toByteArray())
                                .addOnSuccessListener { }
                                .addOnFailureListener {
                                    it.printStackTrace()
                                }
                        }
                        is Uri -> {
                            val bitmap =
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                                    MediaStore.Images.Media.getBitmap(
                                        context.contentResolver,
                                        image
                                    )
                                } else {
                                    val source =
                                        ImageDecoder.createSource(
                                            context.contentResolver,
                                            image
                                        )
                                    ImageDecoder.decodeBitmap(source)
                                }
                            val baos = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                            imageRef.putBytes(baos.toByteArray())
                                .addOnSuccessListener { }
                                .addOnFailureListener {
                                    it.printStackTrace()
                                }
                        }
                        else -> {
                            return UploadImageFailure.WrongFormat.left()
                        }
                    }
                val urlTask =
                    uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        imageRef.downloadUrl
                    }.await()
                return urlTask?.right() ?: UploadImageFailure.UploadTaskFailed.left()
            }.mapLeft { t ->
                t.printStackTrace()
                analytics.recordException(t)
                UploadImageFailure.UnknownFailure(t.message)
            }
        }
    }