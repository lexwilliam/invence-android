package com.lexwilliam.core.manager

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore

class ImageManager(
    private val context: Context
) {
    fun uriToBitmap(uri: Uri): Bitmap {
        return MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }
}