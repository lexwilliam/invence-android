package com.lexwilliam.core.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun getGreetingString(currentTime: Instant = Clock.System.now()): String {
    val localTime = currentTime.toLocalDateTime(TimeZone.currentSystemDefault())
    return when (localTime.hour) {
        in 0..5 -> "Good Morning"
        in 6..11 -> "Good Morning" // Extended morning hours if desired
        in 12..16 -> "Good Afternoon"
        in 17..20 -> "Good Evening"
        else -> "Good Evening"
    }
}