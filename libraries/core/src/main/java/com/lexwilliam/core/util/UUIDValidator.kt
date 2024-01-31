package com.lexwilliam.core.util

import java.util.UUID

fun String?.validateUUID(): UUID {
    return this?.let { UUID.fromString(this) } ?: UUID.randomUUID()
}