package com.lexwilliam.core.extensions

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

fun Instant.toFormatString(format: String): String {
    // Ensure timezone information is included in the `Instant`
    val localDateTime =
        this
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .toJavaLocalDateTime()

    // Create a formatter with Indonesian locale and desired pattern
    val formatter =
        DateTimeFormatter.ofPattern(format)

    // Format the LocalDateTime and return the string
    return localDateTime.format(formatter)
}