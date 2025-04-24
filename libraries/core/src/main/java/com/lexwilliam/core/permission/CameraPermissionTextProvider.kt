package com.lexwilliam.core.permission

class CameraPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return when (isPermanentlyDeclined) {
            true -> "It seems like you have declined the Camera Permission, please enable it in the settings"
            false -> "Camera Permission is required to take photos & scanning barcode"
        }
    }
}