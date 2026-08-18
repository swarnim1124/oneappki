package com.xsc.oneapp.core.dashboard

/**
 * A dashboard stat contributed by the business module that actually owns the data
 * (e.g. feature/attendance owns "attendance", feature/exam owns exam-derived stats).
 * The Dashboard shell never computes this itself.
 */
data class DashboardStatContribution(
    val id: String,
    val title: String,
    val value: String,
    val tag: String
)

/**
 * Extension point for a business feature module to feed a live value into a Dashboard
 * stat card without the Dashboard module depending on that feature module. A feature
 * module implements this and contributes it via Hilt `@Binds @IntoSet` in its own DI
 * module; the Dashboard module only depends on this contract, defined here in :core.
 *
 * [statId] must match one of the Dashboard's known stat ids (see DashboardViewModel)
 * for the contribution to be picked up. Return null from [provideStat] when the
 * module has nothing to show yet (e.g. no session, request failed) so the Dashboard
 * falls back to its placeholder for that stat instead of showing stale/wrong data.
 */
interface DashboardStatProvider {
    val statId: String
    suspend fun provideStat(): DashboardStatContribution?
}

/** One point on the Home tab's "Today's Timeline" - see [DashboardTimelineProvider]. */
data class DashboardTimelinePoint(
    val label: String,
    val state: State
) {
    enum class State { DONE, CURRENT, UPCOMING }
}

/**
 * Extension point for a business feature module to feed today's real schedule into
 * the Dashboard's "Today's Timeline" card, the same way [DashboardStatProvider] feeds
 * a single stat tile - a feature module implements this and joins it via Hilt
 * `@Binds @IntoSet` in its own DI module; :feature:dashboard only depends on this
 * contract, defined here in :core, never on the feature module itself.
 *
 * Return an empty list when the module has nothing to show (no session, request
 * failed, no schedule configured for today) so the Dashboard can hide the card
 * entirely rather than render a fabricated one.
 */
interface DashboardTimelineProvider {
    suspend fun provideTimeline(): List<DashboardTimelinePoint>
}
