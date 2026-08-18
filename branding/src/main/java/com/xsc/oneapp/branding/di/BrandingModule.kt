package com.xsc.oneapp.branding.di

import com.xsc.oneapp.branding.BrandingRepository
import com.xsc.oneapp.branding.DefaultBrandingProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BrandingModule {

    @Binds
    @Singleton
    abstract fun bindBrandingRepository(impl: DefaultBrandingProvider): BrandingRepository
}
