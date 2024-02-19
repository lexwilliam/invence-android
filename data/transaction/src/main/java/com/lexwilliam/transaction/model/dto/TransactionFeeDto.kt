package com.lexwilliam.transaction.model.dto

import com.lexwilliam.transaction.model.TransactionFee

data class TransactionFeeDto(
    val name: String? = null,
    val nominal: Double? = null,
    val percent: Float? = null
) {
    fun toDomain() = TransactionFee(
        name = name ?: "",
        nominal = nominal ?: 0.0,
        percent = percent ?: 0.0f
    )

    companion object {
        fun fromDomain(domain: TransactionFee) = TransactionFeeDto(
            name = domain.name,
            nominal = domain.nominal,
            percent = domain.percent
        )
    }
}