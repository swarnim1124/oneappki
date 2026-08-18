package com.xsc.oneapp.core.permissions

import kotlinx.coroutines.flow.StateFlow

/**
 * Single interface the rest of the app asks "can the signed-in user do X" through.
 *
 * OneApp's RBAC is permission-driven (confirmed against the live backend - see
 * `SessionManager`'s kdoc): access checks compare explicit strings like
 * `"timetable.timetable.view"` against the JWT's `permissions` claim, never a role
 * name. [com.xsc.sdk.auth.SessionManager] already implements this correctly and is
 * the real, tested source of truth - this interface exists so feature and core
 * modules depend on a permission *contract* rather than importing SessionManager (an
 * auth/session-identity type) every time they only need a yes/no permission answer.
 * See [SessionManagerPermissionChecker] for the (only) real implementation.
 *
 * This is defense-in-depth only, exactly as SessionManager.hasPermission already
 * documents: the backend re-checks every write and remains the authority. A `true`
 * here means "show/enable this," never "this call is guaranteed to succeed."
 */
interface PermissionChecker {
    /** True if the current session holds [permission]. */
    fun hasPermission(permission: String): Boolean

    /** True if the current session holds any one of [permissions]. */
    fun hasAnyPermission(vararg permissions: String): Boolean

    /** The full set of permission strings held by the current session, live -
     * reacts to login/logout and token refresh the same way
     * `SessionManager.currentPermissions` does. Empty when signed out. */
    val permissions: StateFlow<List<String>>
}
