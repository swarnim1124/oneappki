package com.xsc.oneapp.feature.exam.navigation

import com.xsc.oneapp.core.navigation.AppDestination
import com.xsc.oneapp.core.navigation.NavigationContribution
import javax.inject.Inject

/**
 * Registers Exam's real top-level route with the app-wide navigation registry - see
 * [NavigationContribution]. `route` must stay identical to `app/navigation/Routes.EXAMS`
 * (== [ExamDestinations.GRAPH_ROUTE]). Both `"exam"` and `"exams"` are accepted, same
 * as `Routes.destinationFor` already does, since the backend has been observed to send
 * either spelling.
 *
 * `requiredPermission` is null - Exam has no confirmed backend permission-string
 * contract yet (unlike Timetable's `TimetablePermissions`); see
 * docs/BACKEND_ENDPOINT_REQUIREMENTS.md #9. Reaching this screen stays gated only on
 * being signed in, exactly as it is today.
 */
class ExamNavigationContribution @Inject constructor() : NavigationContribution {
    override val destination = AppDestination(
        backendKeys = setOf("exams", "exam"),
        route = ExamDestinations.GRAPH_ROUTE,
        label = "Exams"
    )
}
