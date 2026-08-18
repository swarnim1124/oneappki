package com.xsc.oneapp.core.permissions

import com.xsc.sdk.auth.SessionManager
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The only real implementation of [PermissionChecker] - a thin delegate to
 * [SessionManager], which already derives the live permission set from the signed-in
 * user's JWT (see SessionManager.refreshFromToken). No permission logic is
 * duplicated here; this exists purely to give the rest of the app a narrower
 * interface to depend on than the full SessionManager (which also carries identity -
 * email, display name, institution id - unrelated to a permission check).
 */
@Singleton
class SessionManagerPermissionChecker @Inject constructor(
    private val sessionManager: SessionManager
) : PermissionChecker {

    override fun hasPermission(permission: String): Boolean =
        sessionManager.hasPermission(permission)

    override fun hasAnyPermission(vararg permissions: String): Boolean =
        sessionManager.hasAnyPermission(*permissions)

    override val permissions: StateFlow<List<String>> = sessionManager.currentPermissions
}
