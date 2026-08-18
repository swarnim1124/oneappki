package com.xsc.oneapp.feature.fee.navigation

import com.xsc.oneapp.core.navigation.AppDestination
import com.xsc.oneapp.core.navigation.NavigationContribution
import javax.inject.Inject

/**
 * Registers Fees' real top-level route with the app-wide navigation registry - see
 * [NavigationContribution]. `route` must stay identical to
 * `app/navigation/Routes.FEES` - this feature has no internal nested graph (unlike
 * Exam/Attendance/Profile), so `:app` mounts its single screen directly under the
 * literal route `"fees"`; that is the same literal reproduced here, not a new one.
 *
 * `requiredPermission` is null - Fees has no confirmed backend permission-string
 * contract yet; see docs/BACKEND_ENDPOINT_REQUIREMENTS.md #9. Reaching this screen
 * stays gated only on being signed in, exactly as it is today.
 */
class FeeNavigationContribution @Inject constructor() : NavigationContribution {
    override val destination = AppDestination(
        backendKeys = setOf("fees", "fee"),
        route = "fees",
        label = "Fees"
    )
}
