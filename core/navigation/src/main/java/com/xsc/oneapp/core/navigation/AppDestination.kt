package com.xsc.oneapp.core.navigation

/**
 * A top-level, backend-addressable feature entry point - the same thing a Dashboard
 * tile or a backend-supplied module key resolves to today (see
 * `feature/dashboard`'s `GetAccessibleModulesUseCase`/`ModuleItem` and `:app`'s
 * `Routes.destinationFor`). [ModuleItem] remains the real, backend-driven source for
 * *which modules a user's Dashboard shows and in what order* - that is already
 * correctly server-controlled and is not duplicated here (see the architecture audit
 * report: "Backend-driven permissions -> hardcoded role checks" is the anti-pattern
 * to avoid, and `ModuleItem` already avoids it).
 *
 * What this registers instead is the *route-resolution* side: given a backend module
 * key, which in-app route answers it, and which permission (if any) is required to
 * enter it - the piece `Routes.destinationFor` previously hardcoded as a fixed `when`
 * importing every feature's navigation object directly into `:app`. A feature module
 * now contributes this itself via [NavigationContribution].
 */
data class AppDestination(
    /**
     * The backend module key(s) this destination answers to, normalised the same way
     * `Routes.destinationFor` already normalises an incoming key - lowercase, no
     * surrounding slashes or whitespace. More than one entry only where the backend
     * contract itself documents more than one accepted spelling (e.g. `"exam"` and
     * `"exams"` both appear in the m_AAA module list this app has observed).
     */
    val backendKeys: Set<String>,
    /**
     * The feature-owned navigation route to navigate to. Must stay identical to the
     * route already mounted for this module in `app/navigation/RootNavHost.kt` - a
     * feature module cannot depend on `:app` to share that constant directly (see
     * RootNavHost.kt, which mounts every feature graph), so this is a second,
     * independently-real literal, not an invented one.
     */
    val route: String,
    val label: String,
    /**
     * Permission string gating this destination, or null when reaching it isn't
     * gated beyond being signed in. Null for every module today except Timetable -
     * no other feature has a confirmed backend permission-string contract yet (see
     * docs/BACKEND_ENDPOINT_REQUIREMENTS.md #9). A null here is an honest "not yet
     * gated," never a stand-in for "no permission needed" as a designed property.
     */
    val requiredPermission: String? = null
)

/**
 * Extension point for a feature module to register its own real top-level
 * destination with the app-wide navigation registry - the same shape as
 * `com.xsc.oneapp.core.dashboard.DashboardStatProvider`'s extension point: a feature
 * implements this and joins it via Hilt `@Binds @IntoSet` in its own DI module;
 * `:core:navigation` only depends on this contract, never a feature module.
 */
interface NavigationContribution {
    val destination: AppDestination
}
