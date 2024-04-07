package com.lexwilliam.log.model.dto

import android.os.Build
import androidx.annotation.RequiresApi
import com.lexwilliam.core.util.validateUUID
import com.lexwilliam.log.model.LogUpdate
import com.lexwilliam.product.model.Product
import com.lexwilliam.product.model.dto.ProductDto

@RequiresApi(Build.VERSION_CODES.O)
data class LogUpdateDto(
    val uuid: String? = null,
    val old: ProductDto? = null,
    val new: ProductDto? = null
) {
    fun toDomain(): LogUpdate =
        LogUpdate(
            uuid = uuid.validateUUID(),
            old = old?.toDomain() ?: Product(),
            new = new?.toDomain() ?: Product()
        )

    companion object {
        fun fromDomain(log: LogUpdate): LogUpdateDto =
            LogUpdateDto(
                uuid = log.uuid.toString(),
                old = ProductDto.fromDomain(log.old),
                new = ProductDto.fromDomain(log.new)
            )
    }
}