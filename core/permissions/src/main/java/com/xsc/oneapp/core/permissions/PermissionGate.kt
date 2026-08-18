package com.xsc.oneapp.core.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Reaches [PermissionChecker] from a Composable that has no ViewModel of its own to
 * carry it - the same `hiltViewModel()` idiom this codebase already uses to reach a
 * singleton outside a screen's own ViewModel (see app/MainViewModel /
 * RootNavHost.kt's `sessionManager` parameter), rather than introducing a second,
 * EntryPointAccessors-based pattern alongside it.
 */
@HiltViewModel
internal class PermissionCheckerHolder @Inject constructor(
    val checker: PermissionChecker
) : ViewModel()

/**
 * Shows [content] only while the signed-in user holds [permission]; shows [fallback]
 * (nothing, by default) otherwise. Reactive - a permission held at composition time
 * that's lost mid-session (session end, forced logout) flips to [fallback] on the
 * next recomposition without the caller doing anything extra.
 *
 * This is the Compose entry point for the permission engine this module centralizes;
 * see [PermissionChecker] for the underlying contract. Today only Timetable has a
 * confirmed backend permission-string contract to gate on (see
 * feature/timetable's TimetablePermissions and
 * docs/BACKEND_ENDPOINT_REQUIREMENTS.md #9) - this is written generically so any
 * other feature can adopt it the moment it has one, with no change needed here.
 */
@Composable
fun PermissionGate(
    permission: String,
    fallback: @Composable () -> Unit = {},
    holder: PermissionCheckerHolder = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val heldPermissions by holder.checker.permissions.collectAsStateWithLifecycle()
    if (permission in heldPermissions) {
        content()
    } else {
        fallback()
    }
}
