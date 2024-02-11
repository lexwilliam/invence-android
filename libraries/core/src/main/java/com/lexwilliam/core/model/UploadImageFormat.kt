package com.lexwilliam.core.model

import android.graphics.Bitmap
import android.net.Uri

sealed interface UploadImageFormat {
    data class WithUri(val uri: Uri) : UploadImageFormat

    data class WithBitmap(val bitmap: Bitmap) : UploadImageFormat
}