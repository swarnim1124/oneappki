package com.xsc.oneapp.feature.profile.navigation

import com.xsc.oneapp.core.navigation.AppDestination
import com.xsc.oneapp.core.navigation.NavigationContribution
import javax.inject.Inject

/**
 * Registers Profile's real top-level route with the app-wide navigation registry -
 * see [NavigationContribution]. `route` must stay identical to
 * `app/navigation/Routes.destinationFor`'s `"profile"` branch
 * (== [ProfileDestinations.PROFILE_ROUTE]).
 *
 * `requiredPermission` is null - every signed-in user can reach their own profile;
 * there is no permission gate on this route today and this contribution does not add
 * one.
 */
class ProfileNavigationContribution @Inject constructor() : NavigationContribution {
    override val destination = AppDestination(
        backendKeys = setOf("profile"),
        route = ProfileDestinations.PROFILE_ROUTE,
        label = "Profile"
    )
}
