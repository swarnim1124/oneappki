package com.xsc.oneapp.feature.curriculum.di

import com.xsc.oneapp.core.navigation.NavigationContribution
import com.xsc.oneapp.feature.curriculum.data.repository.CurriculumRepositoryImpl
import com.xsc.oneapp.feature.curriculum.domain.repository.CurriculumRepository
import com.xsc.oneapp.feature.curriculum.navigation.CurriculumNavigationContribution
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CurriculumModule {

    @Binds
    @Singleton
    abstract fun bindCurriculumRepository(impl: CurriculumRepositoryImpl): CurriculumRepository

    /** Joins the app-wide navigation registry - see [NavigationContribution]. */
    @Binds
    @IntoSet
    abstract fun bindCurriculumNavigationContribution(
        impl: CurriculumNavigationContribution
    ): NavigationContribution
}
