package com.lexwilliam.home.model

import kotlinx.datetime.Instant
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class MonthAndYear(
    val month: Month,
    val year: Int
) {
    override fun toString(): String {
        return "${month.name} $year"
    }

    companion object {
        fun fromInstant(instant: Instant): MonthAndYear {
            val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            return MonthAndYear(
                month = localDateTime.month,
                year = localDateTime.year
            )
        }

        fun fromString(string: String): MonthAndYear {
            val (month, year) = string.split(" ")
            return MonthAndYear(
                month = Month.valueOf(month),
                year = year.toInt()
            )
        }
    }
}