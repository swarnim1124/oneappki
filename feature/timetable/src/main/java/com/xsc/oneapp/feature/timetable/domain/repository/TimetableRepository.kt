package com.xsc.oneapp.feature.timetable.domain.repository

import com.xsc.oneapp.feature.timetable.domain.model.AcademicCalendar
import com.xsc.oneapp.feature.timetable.domain.model.FacultyAllocation
import com.xsc.oneapp.feature.timetable.domain.model.RoomAllocation
import com.xsc.oneapp.feature.timetable.domain.model.Substitution
import com.xsc.oneapp.feature.timetable.domain.model.TimeSlot
import com.xsc.oneapp.feature.timetable.domain.model.TimetableApproval
import com.xsc.oneapp.feature.timetable.domain.model.TimetableEntry
import com.xsc.oneapp.feature.timetable.domain.model.TimetableExportResult
import com.xsc.oneapp.feature.timetable.domain.model.TimetableFilter
import com.xsc.oneapp.feature.timetable.domain.model.WorkingDay

interface TimetableRepository {
    /** `sm_schedule/timetable/view` with [filter]'s optional parameters (contract
     * v2 §4.1: ttId/sectionId/facultyId/roomId/dayOfWeek/timeSlotId/termId/
     * academicYearId/page/limit). */
    suspend fun getTimetableEntries(filter: TimetableFilter = TimetableFilter()): List<TimetableEntry>

    suspend fun getWorkingDays(): List<WorkingDay>
    suspend fun getTimeSlots(): List<TimeSlot>
    suspend fun getAcademicCalendar(): AcademicCalendar?
    suspend fun getFacultyAllocations(): List<FacultyAllocation>
    suspend fun getRoomAllocations(): List<RoomAllocation>
    suspend fun getSubstitutions(): List<Substitution>
    suspend fun getTimetableApprovals(): List<TimetableApproval>

    /** `sm_schedule/timetable/view` with `exportFormat` set - contract v2 §4.1.
     * `format` is one of `"pdf"`, `"excel"`, `"csv"`. */
    suspend fun exportTimetable(format: String, filter: TimetableFilter = TimetableFilter()): TimetableExportResult

    /** `sm_approval/timetableApproval/add` - submits a DRAFT timetable for review
     * (contract v2 §7.1). Requires `timetable.timetableApproval.add`. */
    suspend fun submitTimetableApproval(timetableId: String, remarks: String? = null): TimetableApproval

    /** `sm_approval/timetableApproval/update` - `action` is `"APPROVE"` or
     * `"REJECT"` (contract v2 §7.1). Requires `timetable.timetableApproval.update`. */
    suspend fun decideTimetableApproval(
        timetableId: String,
        approve: Boolean,
        remarks: String? = null
    ): TimetableApproval

    /** `sm_substitution/substitution/add` (contract v2 §6.1). Requires
     * `timetable.substitution.add`. */
    suspend fun requestSubstitution(
        timetableEntryId: String,
        substituteFacultyId: String,
        reason: String,
        originalFacultyId: String? = null,
        substitutionDate: String? = null,
        remarks: String? = null
    ): Substitution

    /** `sm_substitution/substitution/delete` (contract v2 §6.1 "delete/cancel").
     * Requires `timetable.substitution.add` - no dedicated cancel permission is
     * documented (see TimetablePermissions). */
    suspend fun cancelSubstitution(id: String, reason: String)
}
