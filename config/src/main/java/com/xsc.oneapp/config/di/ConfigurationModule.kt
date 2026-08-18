package com.xsc.oneapp.config.di

import com.xsc.oneapp.config.domain.AppConfigRepository
import com.xsc.oneapp.config.domain.AppConfigRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ConfigurationModule {

    @Binds
    @Singleton
    abstract fun bindAppConfigRepository(
        impl: AppConfigRepositoryImpl
    ): AppConfigRepository
}
