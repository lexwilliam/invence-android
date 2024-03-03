package com.lexwilliam.order.model.local

import com.lexwilliam.db.toInstant
import com.lexwilliam.db.toRealmInstant
import com.lexwilliam.order.model.OrderGroup
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.Index
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.datetime.Instant
import java.util.UUID

internal open class LocalOrderGroup : RealmObject {
    @PrimaryKey
    var uuid: RealmUUID = RealmUUID.random()

    @Index
    var branchUUID: RealmUUID = RealmUUID.random()

    var createdBy: String = ""

    var orders: RealmList<LocalOrder> = realmListOf()

    var taxes: RealmList<LocalOrderTax> = realmListOf()

    var discounts: RealmList<LocalOrderDiscount> = realmListOf()

    var createdAt: RealmInstant? = null
    var deletedAt: RealmInstant? = null
    var completedAt: RealmInstant? = null

    fun toDomain(): OrderGroup =
        OrderGroup(
            uuid = UUID.fromString(uuid.toString()),
            branchUUID = UUID.fromString(branchUUID.toString()),
            createdBy = createdBy,
            orders = orders.map { it.toDomain() },
            taxes = taxes.map { it.toDomain() },
            discounts = discounts.map { it.toDomain() },
            createdAt = createdAt?.toInstant() ?: Instant.DISTANT_PAST,
            deletedAt = deletedAt?.toInstant(),
            completedAt = completedAt?.toInstant()
        )

    companion object {
        fun fromDomain(domain: OrderGroup): LocalOrderGroup =
            LocalOrderGroup().apply {
                uuid = RealmUUID.from(domain.uuid.toString())
                branchUUID = RealmUUID.from(domain.branchUUID.toString())
                createdBy = domain.createdBy
                orders = domain.orders.map { LocalOrder.fromDomain(it) }.toRealmList()
                taxes = domain.taxes.map { LocalOrderTax.fromDomain(it) }.toRealmList()
                discounts = domain.discounts.map { LocalOrderDiscount.fromDomain(it) }.toRealmList()
                createdAt = domain.createdAt.toRealmInstant()
                deletedAt = domain.deletedAt?.toRealmInstant()
                completedAt = domain.completedAt?.toRealmInstant()
            }
    }
}