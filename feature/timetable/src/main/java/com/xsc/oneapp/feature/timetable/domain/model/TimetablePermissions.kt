package com.xsc.oneapp.feature.timetable.domain.model

/**
 * Exact permission strings from the RBAC matrix (contract v2 §9). [SessionManager]'s
 * RBAC is permission-driven, not role-driven (see TimetableNotes.kt) - every
 * show/enable decision in the UI should check one of these via
 * `sessionManager.hasPermission(...)`, never `currentRole`.
 *
 * The matrix documents some entries with a `*` suffix (e.g.
 * `timetable.facultyAllocation.*`) as shorthand for "every actionType on this
 * action" - concrete JWT permission strings are per-actionType
 * (`timetable.facultyAllocation.view`, `.add`, ...), so those are spelled out below
 * rather than checked as a literal wildcard string.
 *
 * `substitution` and `timetableApproval` are asymmetric in the matrix: substitution
 * only documents `.add`/`.view` (no `.update`/`.delete` row), so cancelling a
 * substitution is gated on `.add` here - the same roles (Faculty, HOD, Dean, Admin)
 * already hold it, and no dedicated permission is documented for cancel.
 */
object TimetablePermissions {
    const val WORKING_DAY_VIEW = "timetable.workingDay.view"
    const val WORKING_DAY_MANAGE = "timetable.workingDay.update"

    const val TIME_SLOT_VIEW = "timetable.timeSlot.view"
    const val TIME_SLOT_MANAGE = "timetable.timeSlot.update"

    const val ACADEMIC_CALENDAR_VIEW = "timetable.academicCalendar.view"

    const val TIMETABLE_VIEW = "timetable.timetable.view"
    const val TIMETABLE_ADD = "timetable.timetable.add"
    const val TIMETABLE_UPDATE = "timetable.timetable.update"

    const val FACULTY_ALLOCATION_VIEW = "timetable.facultyAllocation.view"
    const val FACULTY_ALLOCATION_MANAGE = "timetable.facultyAllocation.add"

    const val ROOM_ALLOCATION_VIEW = "timetable.roomAllocation.view"
    const val ROOM_ALLOCATION_MANAGE = "timetable.roomAllocation.add"

    const val SUBSTITUTION_VIEW = "timetable.substitution.view"
    const val SUBSTITUTION_ADD = "timetable.substitution.add"

    const val TIMETABLE_APPROVAL_VIEW = "timetable.timetableApproval.view"
    /** Submit-for-approval - `timetableApproval:add` (DRAFT -> PENDING_APPROVAL). */
    const val TIMETABLE_APPROVAL_ADD = "timetable.timetableApproval.add"
    /** Approve/Reject - `timetableApproval:update`. */
    const val TIMETABLE_APPROVAL_UPDATE = "timetable.timetableApproval.update"
}
