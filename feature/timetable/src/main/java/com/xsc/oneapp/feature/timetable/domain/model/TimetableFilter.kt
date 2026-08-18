package com.xsc.oneapp.feature.timetable.domain.model

/**
 * The optional view parameters `sm_schedule/timetable/view` accepts (contract v2
 * §4.1): `ttId`, `sectionId`, `facultyId`, `roomId`, `dayOfWeek`, `timeSlotId`,
 * `termId`, `academicYearId`, `page`, `limit`. `inst_id` is not here - it is not a
 * user-facing filter, it is the session's own institution (see
 * [TimetableRepositoryImpl.instIdPayload]) and is always sent regardless of this
 * filter's state.
 *
 * All-null/default is "no filter" - the widest view the caller's role permits.
 */
data class TimetableFilter(
    val ttId: String? = null,
    val sectionId: String? = null,
    val facultyId: String? = null,
    val roomId: String? = null,
    val dayOfWeek: String? = null,
    val timeSlotId: String? = null,
    val termId: String? = null,
    val academicYearId: String? = null,
    val page: Int? = null,
    val limit: Int? = null
) {
    /** True when at least one narrowing filter is set - drives the filter bar's
     * "Clear" affordance and the "no classes match" empty state versus the plain
     * "no timetable" one. */
    val isActive: Boolean
        get() = listOfNotNull(
            ttId, sectionId, facultyId, roomId, dayOfWeek, timeSlotId, termId, academicYearId
        ).isNotEmpty()
}

/**
 * Which dimension the weekly grid is organized around. The underlying data is the
 * same flat entry list either way (`m_timetable` has no separate per-perspective
 * endpoint) - switching perspective changes which filter chip drives the grid and
 * which field is promoted to the top of each cell's visual hierarchy, not the axes
 * of the grid itself (day columns x period rows stay fixed, per contract v2 §3.1/§3.2
 * working-day and time-slot configuration).
 */
enum class TimetablePerspective {
    SECTION, FACULTY, ROOM
}
