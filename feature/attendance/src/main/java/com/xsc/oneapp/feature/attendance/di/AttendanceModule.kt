package com.xsc.oneapp.feature.attendance.di

import com.xsc.oneapp.core.dashboard.DashboardStatProvider
import com.xsc.oneapp.core.navigation.NavigationContribution
import com.xsc.oneapp.feature.attendance.data.repository.AttendanceRepositoryImpl
import com.xsc.oneapp.feature.attendance.domain.repository.AttendanceRepository
import com.xsc.oneapp.feature.attendance.navigation.AttendanceNavigationContribution
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AttendanceModule {

    @Binds
    @Singleton
    abstract fun bindAttendanceRepository(impl: AttendanceRepositoryImpl): AttendanceRepository

    /** Contributes the real "attendance" tile on the Dashboard's Home tab - see
     * [AttendanceDashboardStatProvider]. */
    @Binds
    @IntoSet
    abstract fun bindAttendanceDashboardStatProvider(
        impl: AttendanceDashboardStatProvider
    ): DashboardStatProvider

    /** Joins the app-wide navigation registry - see [NavigationContribution]. */
    @Binds
    @IntoSet
    abstract fun bindAttendanceNavigationContribution(
        impl: AttendanceNavigationContribution
    ): NavigationContribution
}
