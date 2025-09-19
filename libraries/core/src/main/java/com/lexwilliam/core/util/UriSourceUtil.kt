package com.lexwilliam.core.util

import android.net.Uri
import arrow.core.Either

enum class UriSourceEnum {
    INTERNAL,
    NETWORK
}

sealed class UriSourceError {
    data object UnknownSource : UriSourceError()
}

fun getUriSource(uri: Uri): Either<UriSourceError, UriSourceEnum> {
    val source =
        when (uri.scheme) {
            "file" -> UriSourceEnum.INTERNAL
            "content" -> UriSourceEnum.INTERNAL
            "http", "https" -> UriSourceEnum.NETWORK
            else -> null
        }
    if (source == null) return Either.Left(UriSourceError.UnknownSource)
    return Either.Right(source)
}