package com.xsc.oneapp.feature.timetable.domain.model

/** sch_course_ops.tb_fac_crs_assignment.
 *
 * [weeklyHours] and [sectionId] are the two fields contract v2 §5.1's `add` payload
 * documents (`{ courseOfferingId, facultyId, sectionId, weeklyHours }`) that the
 * confirmed `view` response (2026-07-31) didn't carry - read defensively the same
 * way every other optional field in this module is, so they populate on any backend
 * that does return them without requiring one. [workloadPercent]/[assignmentRoleId]/
 * [isPrimary] remain the confirmed-real fields. */
data class FacultyAllocation(
    val id: String?,
    val courseOfferingId: String?,
    val facultyId: String?,
    val assignmentRoleId: String?,
    val workloadPercent: String?,
    val isPrimary: String?,
    val remarks: String?,
    val sectionId: String? = null,
    val weeklyHours: String? = null
)
