package com.xsc.oneapp.feature.exam.di

import com.xsc.oneapp.core.navigation.NavigationContribution
import com.xsc.oneapp.feature.exam.data.repository.ExamRepositoryImpl
import com.xsc.oneapp.feature.exam.domain.repository.ExamRepository
import com.xsc.oneapp.feature.exam.navigation.ExamNavigationContribution
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ExamModule {

    @Binds
    @Singleton
    abstract fun bindExamRepository(impl: ExamRepositoryImpl): ExamRepository

    /** Joins the app-wide navigation registry - see [NavigationContribution]. */
    @Binds
    @IntoSet
    abstract fun bindExamNavigationContribution(impl: ExamNavigationContribution): NavigationContribution
}
