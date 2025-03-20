package com.lexwilliam.company.route.form.dialog

sealed interface CompanyFormDialogEvent {
    data class ImageChanged(val bmp: Any?) : CompanyFormDialogEvent

    data class NameChanged(val value: String) : CompanyFormDialogEvent

    data object Dismiss : CompanyFormDialogEvent

    data object Confirm : CompanyFormDialogEvent
}