package com.lexwilliam.core.extensions

fun String.shorten(): String {
    return this.split("-")[0]
}