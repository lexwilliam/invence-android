package com.lexwilliam.company.route.form.dialog

import com.lexwilliam.core.model.UploadImageFormat

sealed interface CompanyFormDialogEvent {
    data class ImageChanged(val format: UploadImageFormat?) : CompanyFormDialogEvent

    data class NameChanged(val value: String) : CompanyFormDialogEvent

    data object Dismiss : CompanyFormDialogEvent

    data object Confirm : CompanyFormDialogEvent
}