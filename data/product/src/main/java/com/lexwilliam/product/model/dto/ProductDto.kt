package com.lexwilliam.product.model.dto

import androidx.core.net.toUri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.lexwilliam.firebase.toKtxInstant
import com.lexwilliam.firebase.toTimestamp
import com.lexwilliam.product.model.Product
import kotlinx.datetime.Instant
import java.util.UUID

data class ProductDto(
    val uuid: String? = null,
    val name: String? = null,
    val description: String? = null,
    @JvmField @PropertyName("category_name")
    val categoryName: String? = null,
    @JvmField @PropertyName("sell_price")
    val sellPrice: Double? = null,
    val items: List<ProductItemDto>? = null,
    @JvmField @PropertyName("image_path")
    val imagePath: String? = null,
    @JvmField @PropertyName("created_at")
    val createdAt: Timestamp? = null,
    @JvmField @PropertyName("updated_at")
    val updatedAt: Timestamp? = null
) {
    fun toDomain() =
        Product(
            uuid = uuid?.let { UUID.fromString(uuid) } ?: UUID.randomUUID(),
            name = name ?: "",
            description = description ?: "",
            categoryName = categoryName ?: "",
            sellPrice = sellPrice ?: 0.0,
            items = items?.map { it.toDomain() } ?: emptyList(),
            imagePath = imagePath?.toUri(),
            createdAt = createdAt?.toKtxInstant() ?: Instant.DISTANT_PAST,
            updatedAt = updatedAt?.toKtxInstant()
        )

    companion object {
        fun fromDomain(domain: Product) =
            ProductDto(
                uuid = domain.uuid.toString(),
                name = domain.name,
                description = domain.description,
                categoryName = domain.categoryName,
                sellPrice = domain.sellPrice,
                items = domain.items.map { ProductItemDto.fromDomain(it) },
                imagePath = domain.imagePath?.toString(),
                createdAt = domain.createdAt.toTimestamp(),
                updatedAt = domain.updatedAt?.toTimestamp()
            )
    }
}