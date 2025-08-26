package com.lexwilliam.product.model.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.lexwilliam.firebase.extensions.toKtxInstant
import com.lexwilliam.firebase.extensions.toTimestamp
import com.lexwilliam.product.model.ProductCategory
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ProductCategoryDto(
    val uuid: String? = null,
    @JvmField @PropertyName("user_uuid") @SerialName("user_uuid")
    val userUUID: String? = null,
    val name: String? = null,
    val products: Map<String, ProductDto>? = null,
    @JvmField
    @PropertyName("created_at")
    @SerialName("created_at")
    @Contextual
    val createdAt: Timestamp? = null,
    @JvmField
    @PropertyName("deleted_at")
    @SerialName("deleted_at")
    @Contextual
    val deletedAt: Timestamp? = null
) {
    fun toDomain() =
        ProductCategory(
            uuid = uuid?.let { UUID.fromString(uuid) } ?: UUID.randomUUID(),
            userUUID = userUUID ?: "",
            name = name ?: "",
            products = products?.map { it.value.toDomain() } ?: emptyList(),
            createdAt = createdAt?.toKtxInstant() ?: Instant.DISTANT_PAST,
            deletedAt = deletedAt?.toKtxInstant()
        )

    companion object {
        fun fromDomain(domain: ProductCategory) =
            ProductCategoryDto(
                uuid = domain.uuid.toString(),
                userUUID = domain.userUUID,
                name = domain.name,
                products =
                    domain.products.associate {
                            product ->
                        product.sku to ProductDto.fromDomain(product)
                    },
                createdAt = domain.createdAt.toTimestamp(),
                deletedAt = domain.deletedAt?.toTimestamp()
            )
    }
}