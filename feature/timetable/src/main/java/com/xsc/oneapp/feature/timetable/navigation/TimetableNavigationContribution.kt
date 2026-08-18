package com.xsc.oneapp.feature.timetable.navigation

import com.xsc.oneapp.core.navigation.AppDestination
import com.xsc.oneapp.core.navigation.NavigationContribution
import com.xsc.oneapp.feature.timetable.domain.model.TimetablePermissions
import javax.inject.Inject

/**
 * Registers Timetable's real top-level route with the app-wide navigation registry -
 * see [NavigationContribution]. `route` must stay identical to
 * `app/navigation/Routes.TIMETABLE`; a feature module cannot depend on `:app` to
 * share that constant directly (see RootNavHost.kt, which mounts every feature's
 * screen/graph), so this is a second, independently-real literal, not an invented
 * one.
 *
 * The only feature with a confirmed backend permission-string contract
 * ([TimetablePermissions] - contract v2 §9), so this is also the only
 * [NavigationContribution] today with a non-null [AppDestination.requiredPermission].
 */
class TimetableNavigationContribution @Inject constructor() : NavigationContribution {
    override val destination = AppDestination(
        backendKeys = setOf("timetable"),
        route = "timetable",
        label = "Timetable",
        requiredPermission = TimetablePermissions.TIMETABLE_VIEW
    )
}
