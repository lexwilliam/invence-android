package com.lexwilliam.firebase.extensions

import com.google.firebase.Timestamp
import kotlinx.datetime.Instant
import java.util.Calendar
import java.util.Date

fun Timestamp.toKtxInstant(): Instant = this.toDate().toKtxInstant()

fun Date.toKtxInstant(): Instant = Instant.fromEpochMilliseconds(time)

fun Instant.toDate(): Date {
    val millis = toEpochMilliseconds()
    return Calendar
        .getInstance()
        .apply { timeInMillis = millis }
        .time
}

fun Instant.toTimestamp(): Timestamp {
    return Timestamp(this.toDate())
}