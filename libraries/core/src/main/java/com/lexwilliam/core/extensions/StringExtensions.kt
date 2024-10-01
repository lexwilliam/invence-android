package com.lexwilliam.core.extensions

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun Double.toCurrency(): String {
    val localeIndonesia = Locale("id", "ID")
    val currencyFormat = NumberFormat.getCurrencyInstance(localeIndonesia)
    currencyFormat.currency = Currency.getInstance("IDR")
    (currencyFormat as DecimalFormat).apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }

    val result = currencyFormat.format(this)
    return result
}