package com.lexwilliam.log.model.dto

import com.google.firebase.firestore.PropertyName
import com.lexwilliam.core.util.validateUUID
import com.lexwilliam.firebase.toDate
import com.lexwilliam.firebase.toKtxInstant
import com.lexwilliam.log.model.DataLog
import kotlinx.datetime.Instant
import java.util.Date

data class LogDto(
    val uuid: String? = null,
    @JvmField @PropertyName("branch_uuid")
    val branchUUID: String? = null,
    val restock: LogRestockDto? = null,
    val sell: LogSellDto? = null,
    val update: LogUpdateDto? = null,
    val add: LogAddDto? = null,
    val delete: LogDeleteDto? = null,
    @JvmField @PropertyName("created_at")
    val createdAt: Date? = null,
    @JvmField @PropertyName("deleted_at")
    val deletedAt: Date? = null
) {
    fun toDomain(): DataLog =
        DataLog(
            uuid = uuid.validateUUID(),
            branchUUID = branchUUID.validateUUID(),
            restock = restock?.toDomain(),
            sell = sell?.toDomain(),
            update = update?.toDomain(),
            add = add?.toDomain(),
            delete = delete?.toDomain(),
            createdAt = createdAt?.toKtxInstant() ?: Instant.DISTANT_PAST,
            deletedAt = deletedAt?.toKtxInstant()
        )

    companion object {
        fun fromDomain(domain: DataLog): LogDto =
            LogDto(
                uuid = domain.uuid.toString(),
                branchUUID = domain.branchUUID.toString(),
                restock = domain.restock?.let { LogRestockDto.fromDomain(it) },
                sell = domain.sell?.let { LogSellDto.fromDomain(it) },
                update = domain.update?.let { LogUpdateDto.fromDomain(it) },
                add = domain.add?.let { LogAddDto.fromDomain(it) },
                delete = domain.delete?.let { LogDeleteDto.fromDomain(it) },
                createdAt = domain.createdAt.toDate(),
                deletedAt = domain.deletedAt?.toDate()
            )
    }
}