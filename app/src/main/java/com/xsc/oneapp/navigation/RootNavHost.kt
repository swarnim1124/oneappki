package com.xsc.oneapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.xsc.oneapp.core.permissions.PermissionGate
import com.xsc.oneapp.feature.dashboard.ui.screen.DashboardScreen
import com.xsc.oneapp.feature.login.ui.effect.ForgotPasswordEffect
import com.xsc.oneapp.feature.login.ui.screen.LoginScreen
import com.xsc.oneapp.feature.login.ui.screen.ResetPasswordScreen
import com.xsc.oneapp.feature.login.ui.screen.VerifyOtpScreen
import com.xsc.oneapp.feature.timetable.domain.model.TimetablePermissions
import com.xsc.sdk.auth.SessionManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.xsc.oneapp.feature.profile.navigation.profileGraph
import com.xsc.oneapp.feature.exam.navigation.examGraph
import com.xsc.oneapp.feature.attendance.navigation.attendanceGraph
import com.xsc.oneapp.feature.curriculum.ui.screen.CurriculumScreen
import com.xsc.oneapp.feature.fee.navigation.feeGraph
import com.xsc.oneapp.feature.timetable.ui.screen.TimetableScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RootNavHost(
    navController: NavHostController,
    mainViewModel: com.xsc.oneapp.MainViewModel = hiltViewModel(),
    sessionManager: SessionManager = mainViewModel.sessionManager
) {
    val navigationRegistry = mainViewModel.navigationRegistry
    // Read auth state ONCE to pick the cold-start destination only. NavHost keys its
    // graph off `startDestination` - if this were reactive (collectAsState), flipping
    // isAuthenticated right after login would rebuild the graph and re-enter
    // "dashboard" a second time, on top of the explicit navigate("dashboard") call
    // LoginScreen already issues. That's what was spawning a second DashboardViewModel
    // (and firing accessibleModules twice) immediately after every login. All
    // post-launch transitions go through the explicit navigate() calls below instead.
    val startDestination = remember { if (sessionManager.isAuthenticated.value) Routes.DASHBOARD else Routes.LOGIN }

    // Reactive auth state: the only place in the app that navigates to Login in
    // response to the session actually ending, rather than every screen that has a
    // sign-out button doing it themselves. Previously `isAuthenticated` was read once
    // here for the cold-start destination and never observed again, so a session that
    // ended *during* use - a forced logout from TokenAuthenticator when refresh fails,
    // for instance - left the user stranded on whatever screen they were on with every
    // subsequent call failing, instead of being routed back to Login (see
    // PRODUCTION_READINESS_FINAL.md risk #4 / Day 6-8 plan: "reactive auth state").
    //
    // Only reacts to a true -> false transition, not to "currently unauthenticated",
    // so it doesn't fire while the user is still working through the pre-auth screens
    // (forgot password / OTP / reset), which are unauthenticated the whole time.
    LaunchedEffect(sessionManager) {
        sessionManager.isAuthenticated.collect { authenticated ->
            // Current destination is only readable from the controller's backstack.
            // If we are ALREADY on Login, do nothing.
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (!authenticated && currentRoute != Routes.LOGIN && currentRoute != null) {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToDashboard = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            val viewModel: com.xsc.oneapp.feature.login.ui.viewmodel.ForgotPasswordViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.effect.collectLatest { effect ->
                    when (effect) {
                        is ForgotPasswordEffect.NavigateToVerifyOtp ->
                            navController.navigate(Routes.verifyOtp(effect.resetToken))
                    }
                }
            }

            com.xsc.oneapp.feature.login.ui.screen.ForgotPasswordScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Routes.VERIFY_OTP,
            arguments = listOf(navArgument("resetToken") { type = NavType.StringType })
        ) {
            VerifyOtpScreen(
                onNavigateToResetPassword = { resetToken ->
                    navController.navigate(Routes.resetPassword(resetToken))
                }
            )
        }

        composable(
            Routes.RESET_PASSWORD,
            arguments = listOf(navArgument("resetToken") { type = NavType.StringType })
        ) {
            ResetPasswordScreen(
                onBackToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onNavigateToModule = { route ->
                    navController.navigate(Routes.destinationFor(route, navigationRegistry))
                },
                // Handled both directly (onLogout button click) for immediate UI
                // feedback and reactively (isAuthenticated listener above) for
                // background/forced session expiration.
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                onChangePassword = { navController.navigate(Routes.FORGOT_PASSWORD) }
            )
        }

        // The Exam feature owns its internal destinations; :app only mounts the graph.
        // Routes.EXAMS is an alias for its public entry point.
        examGraph(navController)

        // The Attendance feature owns its internal destinations; :app only mounts the
        // graph. Routes.ATTENDANCE is an alias for its public entry point.
        attendanceGraph(navController)

        composable(Routes.CURRICULUM) {
            CurriculumScreen(onBack = { navController.popBackStack() })
        }

        feeGraph(
            navController = navController,
            onNavigateToInvoice = { invoiceId -> navController.navigate("invoice_details/$invoiceId") },
            onNavigateToPaymentsLedger = { navController.navigate("payments_ledger") },
            onNavigateToConcessions = { navController.navigate("fee/concessions") },
            onNavigateToRefunds = { navController.navigate("fee/refunds") },
            onNavigateToPenalties = { navController.navigate("fee/penalties") },
            onNavigateToReports = { navController.navigate("fee/reports") },
            onBack = { navController.popBackStack() }
        )

        composable(Routes.TIMETABLE) {
            // The one route in this NavHost gated on a real, confirmed permission
            // (architecture audit Phase 2 - see docs/PRODUCTION_READINESS.md Risk #5).
            // Previously reachable to every signed-in user regardless of
            // TimetablePermissions.TIMETABLE_VIEW, same as every other module here
            // still is - no other feature has a confirmed backend permission-string
            // contract yet to gate on (see docs/BACKEND_ENDPOINT_REQUIREMENTS.md #9).
            PermissionGate(
                permission = TimetablePermissions.TIMETABLE_VIEW,
                fallback = {
                    // No confirmed contract for an in-app "access denied" screen -
                    // pop straight back rather than render a screen with no data
                    // queued behind it.
                    LaunchedEffect(Unit) { navController.popBackStack() }
                }
            ) {
                TimetableScreen(onBack = { navController.popBackStack() })
            }
        }

        composable(Routes.MODULE_PATTERN) { backStackEntry ->
            val moduleName = backStackEntry.arguments?.getString("moduleName") ?: "Unknown"
            com.xsc.oneapp.feature.dashboard.ui.screen.DummyModuleScreen(
                moduleName = moduleName,
                onBack = { navController.popBackStack() }
            )
        }
        
        profileGraph(navController)
    }
}
