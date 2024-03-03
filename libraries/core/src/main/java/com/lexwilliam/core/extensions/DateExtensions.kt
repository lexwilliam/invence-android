package com.lexwilliam.core.extensions

import android.os.Build
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern(format)
        } else {
            TODO("VERSION.SDK_INT < O")
        }

    // Format the LocalDateTime and return the string
    return localDateTime.format(formatter)
}