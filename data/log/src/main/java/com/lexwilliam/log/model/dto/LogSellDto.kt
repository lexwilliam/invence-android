package com.lexwilliam.log.model.dto

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.PropertyName
import com.lexwilliam.core.util.validateUUID
import com.lexwilliam.log.model.LogSell
import com.lexwilliam.order.model.OrderGroup
import com.lexwilliam.order.model.dto.OrderGroupDto
import com.lexwilliam.product.model.dto.ProductDto

@RequiresApi(Build.VERSION_CODES.O)
data class LogSellDto(
    val uuid: String? = null,
    @JvmField @PropertyName("order_group")
    val orderGroup: OrderGroupDto? = null,
    @JvmField @PropertyName("sold_products")
    val soldProducts: List<ProductDto>? = null,
    @JvmField @PropertyName("total_profit")
    val totalProfit: Double? = null
) {
    fun toDomain(): LogSell =
        LogSell(
            uuid = uuid.validateUUID(),
            orderGroup = orderGroup?.toDomain() ?: OrderGroup(),
            soldProducts =
                soldProducts?.map {
                        productItem ->
                    productItem.toDomain()
                } ?: emptyList(),
            totalProfit = totalProfit ?: 0.0
        )

    companion object {
        fun fromDomain(log: LogSell): LogSellDto =
            LogSellDto(
                uuid = log.uuid.toString(),
                orderGroup = OrderGroupDto.fromDomain(log.orderGroup),
                soldProducts = log.soldProducts.map { ProductDto.fromDomain(it) },
                totalProfit = log.totalProfit
            )
    }
}