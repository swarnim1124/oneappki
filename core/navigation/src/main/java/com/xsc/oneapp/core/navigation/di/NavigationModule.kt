package com.xsc.oneapp.core.navigation.di

import com.xsc.oneapp.core.navigation.NavigationContribution
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds

/**
 * Declares the [NavigationContribution] extension point itself, here in the owning
 * `:core:navigation` module rather than in a consuming feature module - unlike
 * `DashboardStatProvider` (whose `@Multibinds` lives in `:feature:dashboard`, the
 * consumer), nothing in `:core:navigation` needs a concrete feature binding to
 * exist, so declaring it at the source of the contract avoids depending on any one
 * feature module being present for `Set<NavigationContribution>` to resolve.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {

    @Multibinds
    abstract fun bindNavigationContributions(): Set<NavigationContribution>
}
