package com.xsc.oneapp.feature.attendance.navigation

import com.xsc.oneapp.core.navigation.AppDestination
import com.xsc.oneapp.core.navigation.NavigationContribution
import javax.inject.Inject

/**
 * Registers Attendance's real top-level route with the app-wide navigation registry -
 * see [NavigationContribution]. `route` must stay identical to
 * `app/navigation/Routes.ATTENDANCE` (== [AttendanceDestinations.GRAPH_ROUTE], the
 * public entry point this feature already exposes for `:app` to mount).
 *
 * `requiredPermission` is null: Attendance has no confirmed backend permission-string
 * contract yet (unlike Timetable's `TimetablePermissions`) - see
 * docs/BACKEND_ENDPOINT_REQUIREMENTS.md #9. Reaching this screen stays gated only on
 * being signed in, exactly as it is today; this contribution does not change that.
 */
class AttendanceNavigationContribution @Inject constructor() : NavigationContribution {
    override val destination = AppDestination(
        backendKeys = setOf("attendance"),
        route = AttendanceDestinations.GRAPH_ROUTE,
        label = "Attendance"
    )
}
