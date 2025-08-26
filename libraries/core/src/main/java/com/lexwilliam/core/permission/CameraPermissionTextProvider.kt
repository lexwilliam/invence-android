package com.lexwilliam.core.permission

import android.content.Context
import com.lexwilliam.core.R
import javax.inject.Inject

class CameraPermissionTextProvider
    @Inject
    constructor(
        private val context: Context
    ) : PermissionTextProvider {
        override fun getDescription(isPermanentlyDeclined: Boolean): String {
            return when (isPermanentlyDeclined) {
                true -> context.getString(R.string.please_enable_camera_permission)
                false -> context.getString(R.string.camera_permission_required)
            }
        }
    }