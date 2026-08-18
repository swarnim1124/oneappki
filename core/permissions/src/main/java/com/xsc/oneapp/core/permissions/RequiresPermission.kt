package com.xsc.oneapp.core.permissions

/**
 * Annotation to structurally define which backend RBAC permission is required 
 * to execute a specific UseCase or ViewModel function. 
 *
 * This acts as documentation and can be used by reflection or lint rules. 
 * Actual runtime enforcement should be done using [requirePermission] DSL.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresPermission(val permission: String)
