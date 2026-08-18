package com.xsc.oneapp.core.permissions

/**
 * Executes the [block] if the user holds the [permission].
 * Throws [PermissionDeniedException] if the user is unauthorized.
 * 
 * This DSL replaces manual if/else checks in ViewModels and UseCases,
 * ensuring strict, structural RBAC enforcement.
 */
inline fun <T> PermissionChecker.requirePermission(
    permission: String,
    block: () -> T
): T {
    if (!hasPermission(permission)) {
        throw PermissionDeniedException(permission)
    }
    return block()
}

/**
 * Executes the [block] if the user holds AT LEAST ONE of the [permissions].
 * Throws [PermissionDeniedException] if the user is unauthorized.
 */
inline fun <T> PermissionChecker.requireAnyPermission(
    vararg permissions: String,
    block: () -> T
): T {
    if (!hasAnyPermission(*permissions)) {
        val missingList = permissions.joinToString(", ")
        throw PermissionDeniedException(missingList, "Missing one of required permissions: $missingList")
    }
    return block()
}
