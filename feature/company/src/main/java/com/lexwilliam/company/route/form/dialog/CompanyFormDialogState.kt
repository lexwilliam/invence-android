package com.lexwilliam.company.route.form.dialog

import com.lexwilliam.core.model.UploadImageFormat

data class CompanyFormDialogState(
    val imageUrl: UploadImageFormat? = null,
    val name: String = ""
)