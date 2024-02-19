package com.lexwilliam.order.di

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.lexwilliam.order.model.local.LocalOrder
import com.lexwilliam.order.model.local.LocalOrderDiscount
import com.lexwilliam.order.model.local.LocalOrderGroup
import com.lexwilliam.order.model.local.LocalOrderItem
import com.lexwilliam.order.model.local.LocalOrderTax
import com.lexwilliam.order.repository.OrderRepository
import com.lexwilliam.order.repository.realmOrderRepository
import com.lexwilliam.order.source.OrderLocalSource
import com.lexwilliam.order.source.realmOrderSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OrderModule {
    @OrderRealm
    @Singleton
    @Provides
    internal fun provideGroupRealm(): Realm {
        return Realm.open(
            RealmConfiguration
                .Builder(
                    schema =
                        setOf(
                            LocalOrderDiscount::class,
                            LocalOrderItem::class,
                            LocalOrder::class,
                            LocalOrderGroup::class,
                            LocalOrderTax::class
                        )
                )
                .name("order.realm")
                .deleteRealmIfMigrationNeeded()
                .build()
        )
    }

    @Singleton
    @Provides
    fun provideOrderLocalSource(
        @OrderRealm realm: Realm,
        analytics: FirebaseCrashlytics
    ): OrderLocalSource = realmOrderSource(realm, analytics)

    @Singleton
    @Provides
    fun provideOrderRepository(source: OrderLocalSource): OrderRepository =
        realmOrderRepository(source)

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class OrderRealm
}