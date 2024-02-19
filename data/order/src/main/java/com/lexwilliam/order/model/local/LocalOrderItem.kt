package com.lexwilliam.order.model.local

import androidx.core.net.toUri
import com.lexwilliam.order.model.OrderItem
import io.realm.kotlin.types.RealmObject

internal open class LocalOrderItem : RealmObject {
    var uuid: String? = null
    var name: String? = null
    var categoryName: String? = null
    var label: String? = null
    var price: Double? = null
    var imagePath: String? = null
    var description: String? = null

    fun toDomain(): OrderItem =
        OrderItem(
            uuid = uuid ?: "",
            name = name ?: "",
            categoryName = categoryName ?: "",
            label = label ?: "",
            price = price ?: 0.0,
            imagePath = imagePath?.toUri(),
            description = description ?: ""
        )

    companion object {
        fun fromDomain(domain: OrderItem): LocalOrderItem =
            LocalOrderItem().apply {
                uuid = domain.uuid
                name = domain.name
                categoryName = domain.categoryName
                label = domain.label
                price = domain.price
                imagePath = domain.imagePath.toString()
                description = domain.description
            }
    }
}