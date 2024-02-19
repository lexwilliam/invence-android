package com.lexwilliam.order.model.local

import com.example.db.toInstant
import com.example.db.toRealmInstant
import com.lexwilliam.order.model.Order
import com.lexwilliam.order.model.OrderItem
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import kotlinx.datetime.Instant
import java.util.UUID

internal open class LocalOrder : RealmObject {
    var uuid: RealmUUID = RealmUUID.random()
    var item: LocalOrderItem? = null
    var discounts: RealmList<LocalOrderDiscount> = realmListOf()
    var quantity: Int? = null
    var refundedQuantity: Int? = null
    var note: String? = null
    var createdAt: RealmInstant? = null
    var updatedAt: RealmInstant? = null
    var deletedAt: RealmInstant? = null

    fun toDomain(): Order =
        Order(
            uuid = UUID.fromString(uuid.toString()),
            item = item?.toDomain() ?: OrderItem(),
            discounts = discounts.map { it.toDomain() },
            quantity = quantity ?: 0,
            refundedQuantity = refundedQuantity ?: 0,
            note = note ?: "",
            createdAt = createdAt?.toInstant() ?: Instant.DISTANT_PAST,
            updatedAt = updatedAt?.toInstant(),
            deletedAt = deletedAt?.toInstant()
        )

    companion object {
        fun fromDomain(domain: Order): LocalOrder =
            LocalOrder().apply {
                uuid = RealmUUID.from(domain.uuid.toString())
                item = LocalOrderItem.fromDomain(domain.item)
                discounts = domain.discounts.map { LocalOrderDiscount.fromDomain(it) }.toRealmList()
                quantity = domain.quantity
                refundedQuantity = domain.refundedQuantity
                note = domain.note
                createdAt = domain.createdAt.toRealmInstant()
                updatedAt = domain.updatedAt?.toRealmInstant()
                deletedAt = domain.deletedAt?.toRealmInstant()
            }
    }
}