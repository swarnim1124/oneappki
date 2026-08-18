package com.xsc.oneapp.feature.curriculum.navigation

import com.xsc.oneapp.core.navigation.AppDestination
import com.xsc.oneapp.core.navigation.NavigationContribution
import javax.inject.Inject

/**
 * Registers Curriculum's real top-level route with the app-wide navigation registry -
 * see [NavigationContribution]. `route` must stay identical to
 * `app/navigation/Routes.CURRICULUM` - this feature has no internal nested graph, so
 * `:app` mounts its single screen directly under the literal route `"curriculum"`.
 * Both `"academics"` (the m_AAA contract id) and `"curriculum"` are accepted, same as
 * `Routes.destinationFor` already does.
 *
 * `requiredPermission` is null - Curriculum has no confirmed backend
 * permission-string contract yet; see docs/BACKEND_ENDPOINT_REQUIREMENTS.md #9.
 * Reaching this screen stays gated only on being signed in, exactly as it is today.
 */
class CurriculumNavigationContribution @Inject constructor() : NavigationContribution {
    override val destination = AppDestination(
        backendKeys = setOf("academics", "curriculum"),
        route = "curriculum",
        label = "Curriculum"
    )
}
