package com.lexwilliam.log.model.dto

import android.os.Build
import androidx.annotation.RequiresApi
import com.lexwilliam.core.util.validateUUID
import com.lexwilliam.log.model.LogAdd
import com.lexwilliam.product.model.Product
import com.lexwilliam.product.model.dto.ProductDto

@RequiresApi(Build.VERSION_CODES.O)
data class LogAddDto(
    val uuid: String? = null,
    val product: ProductDto? = null
) {
    fun toDomain(): LogAdd =
        LogAdd(
            uuid = uuid.validateUUID(),
            product = product?.toDomain() ?: Product()
        )

    companion object {
        fun fromDomain(domain: LogAdd): LogAddDto =
            LogAddDto(
                uuid = domain.uuid.toString(),
                product = ProductDto.fromDomain(domain.product)
            )
    }
}