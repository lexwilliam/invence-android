package com.lexwilliam.order.model.dto

import com.lexwilliam.core.util.validateUUID
import com.lexwilliam.order.model.OrderTax

data class OrderTaxDto(
    val uuid: String? = null,
    val name: String? = null,
    val fixed: Double? = null,
    val percent: Float? = null
) {
    fun toDomain() =
        OrderTax(
            uuid = uuid.validateUUID(),
            name = name ?: "",
            fixed = fixed ?: 0.0,
            percent = percent ?: 0.0f
        )

    companion object {
        fun fromDomain(domain: OrderTax) =
            OrderTaxDto(
                uuid = domain.uuid.toString(),
                name = domain.name,
                fixed = domain.fixed,
                percent = domain.percent
            )
    }
}