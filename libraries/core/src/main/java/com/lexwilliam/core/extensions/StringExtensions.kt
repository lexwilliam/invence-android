package com.lexwilliam.core.extensions

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun Double.toCurrency(): String {
    val localeIndonesia = Locale("id", "ID")
    val currencyFormat = NumberFormat.getCurrencyInstance(localeIndonesia)
    currencyFormat.currency = Currency.getInstance("IDR")
    val result = currencyFormat.format(this)
    return result.substring(0, result.length - 3)
}