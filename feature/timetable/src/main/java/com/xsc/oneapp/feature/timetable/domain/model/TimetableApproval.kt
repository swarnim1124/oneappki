package com.xsc.oneapp.feature.timetable.domain.model

/** sch_timetable.tb_tt header record undergoing a status/approval workflow.
 *
 * [version]/[remarks] are contract v2 §7.1's documented response/request fields
 * (`timetableApproval:add`'s success response carries `version`; both `add` and
 * `update` accept an optional `remarks`) that the confirmed row-shape example
 * didn't include - read defensively like the rest of this module's optional
 * fields. */
data class TimetableApproval(
    val id: String?,
    val institutionId: String?,
    val academicYearId: String?,
    val termId: String?,
    val sectionId: String?,
    val ttCode: String?,
    val statusId: String?,
    val description: String?,
    val version: String? = null,
    val remarks: String? = null
)
