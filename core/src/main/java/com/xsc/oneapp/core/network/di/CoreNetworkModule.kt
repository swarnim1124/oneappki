package com.xsc.oneapp.core.network.di

import com.xsc.oneapp.core.network.CoreNetworkAnalyticsLogger
import com.xsc.sdk.network.analytics.NetworkAnalyticsLogger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreNetworkModule {

    @Binds
    @Singleton
    abstract fun bindNetworkAnalyticsLogger(
        impl: CoreNetworkAnalyticsLogger
    ): NetworkAnalyticsLogger
}
