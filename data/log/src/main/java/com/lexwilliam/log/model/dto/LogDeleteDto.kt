package com.lexwilliam.log.model.dto

import android.os.Build
import androidx.annotation.RequiresApi
import com.lexwilliam.core.util.validateUUID
import com.lexwilliam.log.model.LogDelete
import com.lexwilliam.product.model.Product
import com.lexwilliam.product.model.dto.ProductDto

@RequiresApi(Build.VERSION_CODES.O)
data class LogDeleteDto(
    val uuid: String? = null,
    val product: ProductDto? = null
) {
    fun toDomain(): LogDelete =
        LogDelete(
            uuid = uuid.validateUUID(),
            product = product?.toDomain() ?: Product()
        )

    companion object {
        fun fromDomain(domain: LogDelete): LogDeleteDto =
            LogDeleteDto(
                uuid = domain.uuid.toString(),
                product = ProductDto.fromDomain(domain.product)
            )
    }
}