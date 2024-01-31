package com.lexwilliam.company.route.form.dialog

import android.net.Uri

sealed interface CompanyFormDialogEvent {
    data class ImageChanged(val uri: Uri?): CompanyFormDialogEvent

    data class NameChanged(val value: String): CompanyFormDialogEvent

    data object Dismiss: CompanyFormDialogEvent

    data object Confirm: CompanyFormDialogEvent
}