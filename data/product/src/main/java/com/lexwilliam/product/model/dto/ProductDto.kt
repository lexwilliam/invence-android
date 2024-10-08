package com.lexwilliam.product.model.dto

import androidx.core.net.toUri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.lexwilliam.firebase.toKtxInstant
import com.lexwilliam.firebase.toTimestamp
import com.lexwilliam.product.model.Product
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val sku: String? = null,
    val upc: String? = null,
    val name: String? = null,
    val description: String? = null,
    @JvmField @PropertyName("category_name") @SerialName("category_name")
    val categoryName: String? = null,
    @JvmField @PropertyName("sell_price") @SerialName("sell_price")
    val sellPrice: Double? = null,
    val items: List<ProductItemDto>? = null,
    @JvmField @PropertyName("image_path") @SerialName("image_path")
    val imagePath: String? = null,
    @JvmField
    @PropertyName("created_at")
    @SerialName("created_at")
    @Contextual
    val createdAt: Timestamp? = null,
    @JvmField
    @PropertyName("updated_at")
    @SerialName("updated_at")
    @Contextual
    val updatedAt: Timestamp? = null
) {
    fun toDomain() =
        Product(
            sku = sku ?: "",
            upc = upc ?: "",
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
                sku = domain.sku,
                upc = domain.upc,
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