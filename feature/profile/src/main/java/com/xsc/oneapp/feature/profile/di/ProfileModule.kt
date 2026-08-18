package com.xsc.oneapp.feature.profile.di

import com.xsc.oneapp.core.navigation.NavigationContribution
import com.xsc.oneapp.feature.profile.data.remote.datasource.ProfileRemoteDataSource
import com.xsc.oneapp.feature.profile.data.remote.datasource.ProfileRemoteDataSourceImpl
import com.xsc.oneapp.feature.profile.data.repository.ProfileRepositoryImpl
import com.xsc.oneapp.feature.profile.data.repository.SecurityRepositoryImpl
import com.xsc.oneapp.feature.profile.domain.repository.ProfileRepository
import com.xsc.oneapp.feature.profile.domain.repository.SecurityRepository
import com.xsc.oneapp.feature.profile.navigation.ProfileNavigationContribution
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {

    @Binds
    @Singleton
    abstract fun bindProfileRemoteDataSource(
        impl: ProfileRemoteDataSourceImpl
    ): ProfileRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        impl: ProfileRepositoryImpl
    ): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindSecurityRepository(
        impl: SecurityRepositoryImpl
    ): SecurityRepository

    /** Joins the app-wide navigation registry - see [NavigationContribution]. */
    @Binds
    @IntoSet
    abstract fun bindProfileNavigationContribution(
        impl: ProfileNavigationContribution
    ): NavigationContribution
}
