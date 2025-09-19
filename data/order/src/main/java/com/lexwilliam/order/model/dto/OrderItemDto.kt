package com.lexwilliam.order.model.dto

import androidx.core.net.toUri
import com.google.firebase.firestore.PropertyName
import com.lexwilliam.order.model.OrderItem
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class OrderItemDto(
    val uuid: String? = null,
    val upc: String? = null,
    val name: String? = null,
    @JvmField @PropertyName("category_name")
    val categoryName: String? = null,
    val label: String? = null,
    val price: Double? = null,
    @JvmField @PropertyName("image_path")
    val imagePath: String? = null,
    val description: String? = null
) {
    fun toDomain() =
        OrderItem(
            uuid = uuid ?: "",
            upc = upc,
            name = name ?: "",
            categoryName = categoryName ?: "",
            label = label ?: "",
            price = price ?: 0.0,
            imagePath = imagePath?.toUri(),
            description = description ?: ""
        )

    companion object {
        fun fromDomain(domain: OrderItem) =
            OrderItemDto(
                uuid = domain.uuid,
                name = domain.name,
                categoryName = domain.categoryName,
                label = domain.label,
                price = domain.price,
                imagePath = domain.imagePath.toString(),
                description = domain.description
            )
    }
}