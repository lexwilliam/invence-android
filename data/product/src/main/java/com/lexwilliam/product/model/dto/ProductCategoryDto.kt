package com.lexwilliam.product.model.dto

import androidx.core.net.toUri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.lexwilliam.firebase.toKtxInstant
import com.lexwilliam.firebase.toTimestamp
import com.lexwilliam.product.model.ProductCategory
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ProductCategoryDto(
    val uuid: String? = null,
    @JvmField @PropertyName("branch_uuid") @SerialName("branch_uuid")
    val branchUUID: String? = null,
    val name: String? = null,
    @JvmField @PropertyName("image_url") @SerialName("image_url")
    val imageUrl: String? = null,
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
            branchUUID = branchUUID?.let { UUID.fromString(branchUUID) } ?: UUID.randomUUID(),
            name = name ?: "",
            imageUrl = imageUrl?.toUri(),
            products = products?.map { it.value.toDomain() } ?: emptyList(),
            createdAt = createdAt?.toKtxInstant() ?: Instant.DISTANT_PAST,
            deletedAt = deletedAt?.toKtxInstant()
        )

    companion object {
        fun fromDomain(domain: ProductCategory) =
            ProductCategoryDto(
                uuid = domain.uuid.toString(),
                branchUUID = domain.branchUUID.toString(),
                name = domain.name,
                imageUrl = domain.imageUrl?.toString(),
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