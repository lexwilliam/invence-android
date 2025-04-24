package com.lexwilliam.core.permission

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}