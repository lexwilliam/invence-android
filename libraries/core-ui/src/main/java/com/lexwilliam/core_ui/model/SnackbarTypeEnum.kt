package com.lexwilliam.core_ui.model

enum class SnackbarTypeEnum {
    SUCCESS,
    ERROR;

    override fun toString(): String {
        return when (this) {
            SUCCESS -> "SUCCESS"
            ERROR -> "ERROR"
        }
    }
}