package com.lexwilliam.company.route.form.dialog

import android.graphics.Bitmap

sealed interface CompanyFormDialogEvent {
    data class ImageChanged(val bmp: Bitmap?) : CompanyFormDialogEvent

    data class NameChanged(val value: String) : CompanyFormDialogEvent

    data object Dismiss : CompanyFormDialogEvent

    data object Confirm : CompanyFormDialogEvent
}