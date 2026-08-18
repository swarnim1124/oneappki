package com.xsc.oneapp.plugins

/**
 * Generalises the extension-point shape this codebase already uses three times over
 * (`core.dashboard.DashboardStatProvider`/`DashboardTimelineProvider`,
 * `core.navigation.NavigationContribution`: a feature module implements a small
 * interface and joins a Hilt `Set<T>` via `@Binds @IntoSet`, with the consuming
 * module depending only on the contract, never the feature) into one named concept,
 * for the architecture spec's §27 future-module list (AI Advisor, Chat, Biometric
 * Attendance, Virtual ID Card, Digital Wallet, LMS, Payment Gateway, Video Streaming)
 * to register against without inventing a fourth ad-hoc Set<T> shape per module.
 *
 * The three extension points above are deliberately left as they are, not retrofitted
 * onto this - they are real, tested, working code with no compiler available in this
 * environment to verify a refactor against; [FeaturePlugin]/[PluginRegistry] are for
 * a module that does not exist yet to adopt from day one, per the audit's
 * "do not silently change working behaviour" rule.
 */
interface FeaturePlugin {
    /** Stable identifier for this plugin, e.g. `"chat"`, `"biometric_attendance"` -
     * used as [PluginRegistry]'s lookup key. Must be unique across every registered
     * plugin of the same [PluginRegistry] instance. */
    val pluginId: String
}
