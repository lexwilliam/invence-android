package com.lexwilliam.order.model.local

import com.lexwilliam.order.model.OrderDiscount
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import java.util.UUID

internal open class LocalOrderDiscount : RealmObject {
    var uuid: RealmUUID = RealmUUID.random()
    var name: String? = null
    var fixed: Double? = null
    var percent: Float? = null

    fun toDomain(): OrderDiscount =
        OrderDiscount(
            uuid = UUID.fromString(uuid.toString()),
            name = name ?: "",
            fixed = fixed ?: 0.0,
            percent = percent ?: 0.0f
        )

    companion object {
        fun fromDomain(domain: OrderDiscount): LocalOrderDiscount =
            LocalOrderDiscount().apply {
                uuid = RealmUUID.from(domain.uuid.toString())
                name = domain.name
                fixed = domain.fixed
                percent = domain.percent
            }
    }
}