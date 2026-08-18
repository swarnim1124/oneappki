package com.xsc.oneapp.feature.timetable.di

import com.xsc.oneapp.core.dashboard.DashboardStatProvider
import com.xsc.oneapp.core.dashboard.DashboardTimelineProvider
import com.xsc.oneapp.core.navigation.NavigationContribution
import com.xsc.oneapp.feature.timetable.dashboard.TimetableDashboardStatProvider
import com.xsc.oneapp.feature.timetable.dashboard.TimetableTimelineProvider
import com.xsc.oneapp.feature.timetable.data.repository.TimetableRepositoryImpl
import com.xsc.oneapp.feature.timetable.domain.repository.TimetableRepository
import com.xsc.oneapp.feature.timetable.navigation.TimetableNavigationContribution
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimetableModule {

    @Binds
    @Singleton
    abstract fun bindTimetableRepository(impl: TimetableRepositoryImpl): TimetableRepository

    /** Joins the Dashboard module's stat-provider extension point (see
     * [DashboardStatProvider]) so the Home tab's "Next Class" card reflects a real
     * upcoming class instead of its static placeholder. */
    @Binds
    @IntoSet
    abstract fun bindTimetableDashboardStatProvider(impl: TimetableDashboardStatProvider): DashboardStatProvider

    /** Joins the Dashboard module's timeline extension point (see
     * [DashboardTimelineProvider]) so "Today's Timeline" reflects the day's real
     * schedule instead of 4 hardcoded points. */
    @Binds
    @IntoSet
    abstract fun bindTimetableTimelineProvider(impl: TimetableTimelineProvider): DashboardTimelineProvider

    /** Joins the app-wide navigation registry (see [NavigationContribution]) so
     * `Routes.destinationFor` can resolve to Timetable's real, permission-gated
     * route without `:app` importing this module's navigation package directly. */
    @Binds
    @IntoSet
    abstract fun bindTimetableNavigationContribution(
        impl: TimetableNavigationContribution
    ): NavigationContribution
}
