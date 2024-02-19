package com.lexwilliam.order.source

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.lexwilliam.order.model.OrderGroup
import com.lexwilliam.order.model.local.LocalOrderGroup
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.types.RealmUUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

interface OrderLocalSource {
    fun observeOrderGroup(branchUUID: UUID): Flow<List<OrderGroup>>

    fun observeSingleOrderGroup(uuid: UUID): Flow<OrderGroup?>

    suspend fun upsertOrderGroup(order: OrderGroup): Either<String, OrderGroup>

    suspend fun deleteOrderGroup(order: OrderGroup): Either<String, OrderGroup>
}

fun realmOrderSource(
    realm: Realm,
    analytics: FirebaseCrashlytics
) = object : OrderLocalSource {
    override fun observeOrderGroup(branchUUID: UUID): Flow<List<OrderGroup>> {
        val mappedUUID = RealmUUID.from(branchUUID.toString())

        return realm
            .query(LocalOrderGroup::class, "branchUUID == $0", mappedUUID)
            .asFlow()
            .map { changeset -> changeset.list.map { order -> order.toDomain() } }
    }

    override fun observeSingleOrderGroup(uuid: UUID): Flow<OrderGroup?> {
        val mappedUUID = RealmUUID.from(uuid.toString())

        return realm
            .query(LocalOrderGroup::class, "uuid == $0", mappedUUID)
            .first()
            .asFlow()
            .map { result -> result.obj?.toDomain() }
    }

    override suspend fun upsertOrderGroup(order: OrderGroup): Either<String, OrderGroup> {
        return Either.catch {
            realm.write {
                val toUpsert = LocalOrderGroup.fromDomain(order)
                copyToRealm(toUpsert, UpdatePolicy.ALL)
            }
            return order.right()
        }.mapLeft { throwable ->
            analytics.recordException(throwable)
            return throwable.message.orEmpty().left()
        }
    }

    override suspend fun deleteOrderGroup(order: OrderGroup): Either<String, OrderGroup> {
        return Either.catch {
            realm.write {
                val mappedUUID = RealmUUID.from(order.uuid.toString())

                val groupQuery =
                    this
                        .query(LocalOrderGroup::class, "uuid == $0", mappedUUID)
                        .find()
                        .firstOrNull()

                groupQuery?.let { delete(groupQuery) }
            }

            return order.right()
        }.mapLeft { throwable ->
            analytics.recordException(throwable)
            return throwable.message.orEmpty().left()
        }
    }
}