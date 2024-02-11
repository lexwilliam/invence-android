package com.lexwilliam.core.util

import java.util.Locale

fun generateCompanyId(companyName: String): String {
    if (companyName.isEmpty()) return ""
    // Convert the company name to lowercase and replace spaces with underscores
    val formattedCompanyName = companyName.lowercase(Locale.getDefault()).replace(" ", "_")

    // Generate a random 5-digit number
    val randomDigits = (10000..99999).random()

    // Create the company ID by combining the formatted company name and random digits
    val companyId = "$formattedCompanyName#$randomDigits"

    return companyId
}