package com.xsc.oneapp.navigation

import android.net.Uri
import com.xsc.oneapp.core.navigation.NavigationRegistry
import com.xsc.oneapp.feature.attendance.navigation.AttendanceDestinations
import com.xsc.oneapp.feature.exam.navigation.ExamDestinations
import com.xsc.oneapp.feature.profile.navigation.ProfileDestinations

/** Navigation route constants for RootNavHost's top-level graph. */
object Routes {
    const val LOGIN = "login"
    const val FORGOT_PASSWORD = "forgot_password"
    const val VERIFY_OTP = "verify_otp/{resetToken}"
    const val RESET_PASSWORD = "reset_password/{resetToken}"
    const val DASHBOARD = "dashboard"

    /** Exam is a nested graph owned by :feature:exam - navigating here lands on its
     * start destination (the overview). Its internal routes, including Hall Ticket,
     * are not visible to :app. */
    const val EXAMS = ExamDestinations.GRAPH_ROUTE

    /** Attendance is a nested graph owned by :feature:attendance - navigating here
     * lands on its start destination (the overview). Its internal routes are not
     * visible to :app. */
    const val ATTENDANCE = AttendanceDestinations.GRAPH_ROUTE

    const val CURRICULUM = "curriculum"
    const val FEES = "fees"
    const val TIMETABLE = "timetable"
    const val MODULE_PATTERN = "module/{moduleName}"

    fun module(name: String) = "module/$name"

    /**
     * Resolves a backend-supplied module route (`"/fees"`, `"exams"`, `"/Academics"`)
     * to an in-app destination.
     *
     * Matching is done on a normalised key - lowercased, with surrounding slashes and
     * whitespace stripped - because the previous `when (route.removePrefix("/"))` was
     * an exact, case-sensitive match. Anything it didn't match fell through to the
     * generic module template, so a backend that returned `"/Fees"` or `"fees/"`
     * produced a "Fees Module — Template Active" placeholder that looked, to a user,
     * exactly like the Fees feature being missing.
     *
     * Singular aliases are accepted for the same reason, and `curriculum` is accepted
     * alongside `academics` so the backend can adopt the new name without a client
     * release. The m_AAA contract id remains `academics`.
     *
     * The `when` below stays the primary, unconditional resolver for every module
     * this app has ever shipped a screen for - zero behavioural change from before.
     * [registry] (architecture audit Phase 2 - see `core/navigation`) only takes over
     * for a key none of those branches recognise: today that is still always the
     * generic module template, since every feature module registers the exact same
     * key set already listed here, but a feature added later needs no change to this
     * `when` to become reachable - only its own `NavigationContribution` binding, the
     * "dynamic module resolution" property `:app` previously had none of.
     */
    fun destinationFor(route: String, registry: NavigationRegistry): String =
        when (val key = route.trim().trim('/').lowercase()) {
            "profile" -> ProfileDestinations.PROFILE_ROUTE
            "exams", "exam" -> EXAMS
            "fees", "fee" -> FEES
            "attendance" -> ATTENDANCE
            "academics", "curriculum" -> CURRICULUM
            "timetable" -> TIMETABLE
            else -> registry.routeFor(key) ?: module(key)
        }

    fun verifyOtp(resetToken: String) = "verify_otp/${Uri.encode(resetToken)}"

    fun resetPassword(resetToken: String) = "reset_password/${Uri.encode(resetToken)}"
}
