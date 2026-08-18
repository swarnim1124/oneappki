package com.xsc.oneapp.feature.fee.di

import com.xsc.oneapp.core.dashboard.DashboardStatProvider
import com.xsc.oneapp.core.navigation.NavigationContribution
import com.xsc.oneapp.feature.fee.dashboard.FeeDashboardStatProvider
import com.xsc.oneapp.feature.fee.data.repository.FeeRepositoryImpl
import com.xsc.oneapp.feature.fee.domain.repository.FeeRepository
import com.xsc.oneapp.feature.fee.navigation.FeeNavigationContribution
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeeModule {

    @Binds
    @Singleton
    abstract fun bindFeeRepository(impl: FeeRepositoryImpl): FeeRepository

    /** Joins the Dashboard module's stat-provider extension point (see
     * [DashboardStatProvider]) so the Home tab's "PENDING FEES" card reflects the
     * same outstanding balance as the Fees module instead of its static placeholder. */
    @Binds
    @IntoSet
    abstract fun bindFeeDashboardStatProvider(impl: FeeDashboardStatProvider): DashboardStatProvider

    /** Joins the app-wide navigation registry - see [NavigationContribution]. */
    @Binds
    @IntoSet
    abstract fun bindFeeNavigationContribution(impl: FeeNavigationContribution): NavigationContribution
}
