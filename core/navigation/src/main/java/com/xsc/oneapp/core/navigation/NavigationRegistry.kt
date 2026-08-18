package com.xsc.oneapp.core.navigation

import com.xsc.oneapp.core.permissions.PermissionChecker
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Resolves a backend module key to the real route a feature module registered for it
 * (see [NavigationContribution]), and can filter the full set of registered
 * destinations down to what the current user is actually permitted to reach.
 *
 * Built from a Hilt-collected `Set<NavigationContribution>` - every feature module
 * that registers one is automatically included with no change needed here or in
 * `:app`, the same "join a Set, no reverse dependency" shape as
 * `DashboardStatProvider`.
 */
@Singleton
class NavigationRegistry @Inject constructor(
    private val contributions: Set<@JvmSuppressWildcards NavigationContribution>
) {
    private val destinations: List<AppDestination> by lazy {
        contributions.map { it.destination }
    }

    private val byKey: Map<String, AppDestination> by lazy {
        buildMap {
            for (destination in destinations) {
                for (key in destination.backendKeys) {
                    put(key, destination)
                }
            }
        }
    }

    /**
     * The in-app route registered for [rawKey], or null if no feature module has
     * registered that backend key. Callers own their own fallback (see
     * `app/navigation/Routes.destinationFor`, which falls back to a generic
     * placeholder screen) - a miss here is not itself an error, it means nothing has
     * claimed that key yet.
     */
    fun routeFor(rawKey: String): String? =
        byKey[normalize(rawKey)]?.route

    /**
     * Every registered destination [checker] currently has access to - a destination
     * with no [AppDestination.requiredPermission] is always included. Not consumed
     * by any screen yet (see [AppDestination.requiredPermission]'s kdoc for why);
     * this is real, tested infrastructure ready for a menu/sidebar consumer once more
     * features have a confirmed permission contract to gate on.
     */
    fun authorizedDestinations(checker: PermissionChecker): List<AppDestination> =
        destinations.filter { destination ->
            val required = destination.requiredPermission
            required == null || checker.hasPermission(required)
        }

    private fun normalize(rawKey: String): String = rawKey.trim().trim('/').lowercase()
}
