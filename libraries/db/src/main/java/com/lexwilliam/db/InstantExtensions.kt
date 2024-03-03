package com.lexwilliam.db

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import kotlinx.datetime.Instant

/**
 * Model class that stores an Instant (kotlinx-datetime) field as a RealmInstant via a conversion
 *
 * @see <a href = https://www.mongodb.com/docs/realm/sdk/kotlin/realm-database/schemas/timestamps/> Realm Documentation </a>
 */
class RealmInstantConversion : RealmObject {
    private var _timestamp: RealmInstant = RealmInstant.from(0, 0)
    var timestamp: Instant
        get() {
            return _timestamp.toInstant()
        }
        set(value) {
            _timestamp = value.toRealmInstant()
        }
}

/**
 * Conversion function for [RealmInstant] to [Instant]
 *
 * @see <a href = https://www.mongodb.com/docs/realm/sdk/kotlin/realm-database/schemas/timestamps/> Realm Documentation </a>
 */
fun RealmInstant.toInstant(): Instant {
    val sec: Long = this.epochSeconds
    val nano: Int = this.nanosecondsOfSecond

    return if (sec >= 0) {
        Instant.fromEpochSeconds(sec, nano.toLong())
    } else {
        Instant.fromEpochSeconds(sec - 1, 1000000 + nano.toLong())
    }
}

/**
 * Conversion function for [Instant] to [RealmInstant]
 *
 * @see <a href = https://www.mongodb.com/docs/realm/sdk/kotlin/realm-database/schemas/timestamps/> Realm Documentation </a>
 */
fun Instant.toRealmInstant(): RealmInstant {
    val sec: Long = this.epochSeconds
    val nano: Int = this.nanosecondsOfSecond

    return if (sec >= 0) {
        RealmInstant.from(sec, nano)
    } else {
        RealmInstant.from(sec + 1, -1000000 + nano)
    }
}