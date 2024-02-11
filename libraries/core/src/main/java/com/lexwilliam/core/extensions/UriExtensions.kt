package com.lexwilliam.core.extensions

import android.net.Uri
import com.lexwilliam.core.model.UploadImageFormat

fun UploadImageFormat?.isFirebaseUri(): Boolean {
    return when (val format = this) {
        is UploadImageFormat.WithUri -> format.uri.isFirebaseUri()
        is UploadImageFormat.WithBitmap -> false
        else -> false
    }
}

fun Uri.isFirebaseUri(): Boolean {
    return this.toString().startsWith("https://firebasestorage.googleapis.com")
}