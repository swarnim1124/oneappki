package com.xsc.oneapp.core.permissions.di

import com.xsc.oneapp.core.permissions.PermissionChecker
import com.xsc.oneapp.core.permissions.SessionManagerPermissionChecker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PermissionModule {

    @Binds
    @Singleton
    abstract fun bindPermissionChecker(impl: SessionManagerPermissionChecker): PermissionChecker
}
