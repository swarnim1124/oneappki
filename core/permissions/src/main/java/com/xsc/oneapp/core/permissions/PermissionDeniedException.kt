package com.xsc.oneapp.core.permissions

/**
 * Thrown when a user attempts to execute a ViewModel or UseCase operation
 * without holding the required backend RBAC permission.
 */
class PermissionDeniedException(
    val missingPermission: String,
    message: String = "Missing required permission: $missingPermission"
) : SecurityException(message)
