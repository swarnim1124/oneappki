package com.xsc.oneapp.feature.timetable.data.mapper

import com.google.gson.JsonObject
import com.xsc.oneapp.core.json.JsonRowUtils
import com.xsc.oneapp.feature.timetable.domain.model.AcademicCalendar
import com.xsc.oneapp.feature.timetable.domain.model.AcademicCalendarAdjustment
import com.xsc.oneapp.feature.timetable.domain.model.FacultyAllocation
import com.xsc.oneapp.feature.timetable.domain.model.RoomAllocation
import com.xsc.oneapp.feature.timetable.domain.model.Substitution
import com.xsc.oneapp.feature.timetable.domain.model.TimeSlot
import com.xsc.oneapp.feature.timetable.domain.model.TimetableApproval
import com.xsc.oneapp.feature.timetable.domain.model.TimetableEntry
import com.xsc.oneapp.feature.timetable.domain.model.TimetableExportResult
import com.xsc.oneapp.feature.timetable.domain.model.WorkingDay

/** m_timetable rows are raw SQLAlchemy-model dictionaries (same pattern as
 * m_attendance/m_curriculum/m_fees) - field names verified against the real
 * confirmed response shapes (2026-07-31). Fields added after that date (see each
 * model's doc comment) read plausible contract-documented keys defensively and
 * default to null rather than assuming any backend returns them. */
fun JsonObject.toTimetableEntry(): TimetableEntry = TimetableEntry(
    id = JsonRowUtils.firstString(this, "id"),
    ttId = JsonRowUtils.firstString(this, "tt_id", "ttId"),
    institutionId = JsonRowUtils.firstString(this, "inst_id", "instId"),
    academicYearId = JsonRowUtils.firstString(this, "academic_year_id", "academicYearId"),
    termId = JsonRowUtils.firstString(this, "term_id", "termId"),
    programId = JsonRowUtils.firstString(this, "program_id", "programId"),
    semesterId = JsonRowUtils.firstString(this, "semester_id", "semesterId"),
    sectionId = JsonRowUtils.firstString(this, "sec_id", "secId"),
    courseId = JsonRowUtils.firstString(this, "course_id", "courseId"),
    courseOfferingId = JsonRowUtils.firstString(this, "crs_offering_id", "crsOfferingId"),
    facultyId = JsonRowUtils.firstString(this, "fac_id", "facId"),
    facultyCourseAssignmentId = JsonRowUtils.firstString(this, "fac_crs_assignment_id", "facCrsAssignmentId"),
    workingDayId = JsonRowUtils.firstString(this, "working_day_id", "workingDayId"),
    dayOfWeek = JsonRowUtils.firstString(this, "day_of_week", "dayOfWeek"),
    timeSlotId = JsonRowUtils.firstString(this, "time_slot_id", "timeSlotId"),
    roomId = JsonRowUtils.firstString(this, "room_id", "roomId"),
    sessionTypeId = JsonRowUtils.firstString(this, "session_type_id", "sessionTypeId"),
    startDate = JsonRowUtils.firstString(this, "start_date", "startDate"),
    endDate = JsonRowUtils.firstString(this, "end_date", "endDate"),
    isActive = JsonRowUtils.firstString(this, "is_active", "isActive"),
    ttCode = JsonRowUtils.firstString(this, "tt_code", "ttCode"),
    ttStatus = JsonRowUtils.firstString(this, "tt_status", "ttStatus", "status_id", "statusId"),
    ttVersionNo = JsonRowUtils.firstString(this, "version_no", "versionNo", "tt_version_no"),
    ttEffectiveFrom = JsonRowUtils.firstString(this, "tt_effective_from", "effectiveFrom", "effective_from"),
    ttEffectiveTo = JsonRowUtils.firstString(this, "tt_effective_to", "effectiveTo", "effective_to"),
    ttDescription = JsonRowUtils.firstString(this, "tt_description", "description")
)

fun JsonObject.toWorkingDay(): WorkingDay = WorkingDay(
    id = JsonRowUtils.firstString(this, "id"),
    institutionId = JsonRowUtils.firstString(this, "inst_id", "instId"),
    academicYearId = JsonRowUtils.firstString(this, "academic_year_id", "academicYearId"),
    name = JsonRowUtils.firstString(this, "name"),
    effectiveFrom = JsonRowUtils.firstString(this, "effective_from", "effectiveFrom"),
    effectiveTo = JsonRowUtils.firstString(this, "effective_to", "effectiveTo"),
    dayOfWeekId = JsonRowUtils.firstString(this, "day_of_week_id", "dayOfWeekId"),
    dayName = JsonRowUtils.firstString(this, "day_name", "dayName"),
    isWorkingDay = JsonRowUtils.firstString(this, "is_working_day", "isWorkingDay"),
    isActive = JsonRowUtils.firstString(this, "is_active", "isActive")
)

fun JsonObject.toTimeSlot(): TimeSlot = TimeSlot(
    id = JsonRowUtils.firstString(this, "id"),
    institutionId = JsonRowUtils.firstString(this, "inst_id", "instId"),
    slotName = JsonRowUtils.firstString(this, "slot_name", "slotName"),
    startTime = JsonRowUtils.firstString(this, "start_time", "startTime"),
    endTime = JsonRowUtils.firstString(this, "end_time", "endTime"),
    slotSequence = JsonRowUtils.firstString(this, "slot_sequence", "slotSequence", "slot_order", "slotOrder"),
    isBreak = JsonRowUtils.firstString(this, "is_break", "isBreak"),
    isActive = JsonRowUtils.firstString(this, "is_active", "isActive")
)

fun JsonObject.toAcademicCalendar(): AcademicCalendar = AcademicCalendar(
    institutionId = JsonRowUtils.firstString(this, "inst_id", "instId"),
    academicYearId = JsonRowUtils.firstString(this, "academic_year_id", "academicYearId"),
    termId = JsonRowUtils.firstString(this, "term_id", "termId", "acad_term_id", "acadTermId"),
    note = JsonRowUtils.firstString(this, "note"),
    adjustments = JsonRowUtils.firstRowArray(this, "adjustments", "holiday_adjustments", "holidayAdjustments", "rows")
        .map { it.toAcademicCalendarAdjustment() }
)

fun JsonObject.toAcademicCalendarAdjustment(): AcademicCalendarAdjustment = AcademicCalendarAdjustment(
    id = JsonRowUtils.firstString(this, "id"),
    acadTermId = JsonRowUtils.firstString(this, "acad_term_id", "acadTermId", "term_id", "termId"),
    holidayDate = JsonRowUtils.firstString(this, "holiday_date", "holidayDate"),
    adjustmentTypeId = JsonRowUtils.firstString(this, "adjustment_type_id", "adjustmentTypeId"),
    sourceWorkingDayId = JsonRowUtils.firstString(this, "source_working_day_id", "sourceWorkingDayId"),
    targetWorkingDayId = JsonRowUtils.firstString(this, "target_working_day_id", "targetWorkingDayId"),
    targetDate = JsonRowUtils.firstString(this, "target_date", "targetDate"),
    remarks = JsonRowUtils.firstString(this, "remarks")
)

fun JsonObject.toFacultyAllocation(): FacultyAllocation = FacultyAllocation(
    id = JsonRowUtils.firstString(this, "id"),
    courseOfferingId = JsonRowUtils.firstString(this, "crs_offering_id", "crsOfferingId"),
    facultyId = JsonRowUtils.firstString(this, "fac_id", "facId"),
    assignmentRoleId = JsonRowUtils.firstString(this, "assignment_role_id", "assignmentRoleId"),
    workloadPercent = JsonRowUtils.firstString(this, "workload_percent", "workloadPercent"),
    isPrimary = JsonRowUtils.firstString(this, "is_primary", "isPrimary"),
    remarks = JsonRowUtils.firstString(this, "remarks"),
    sectionId = JsonRowUtils.firstString(this, "sec_id", "secId", "sectionId"),
    weeklyHours = JsonRowUtils.firstString(this, "weekly_hours", "weeklyHours")
)

fun JsonObject.toRoomAllocation(): RoomAllocation = RoomAllocation(
    id = JsonRowUtils.firstString(this, "id"),
    ttId = JsonRowUtils.firstString(this, "tt_id", "ttId"),
    roomId = JsonRowUtils.firstString(this, "room_id", "roomId"),
    dayOfWeek = JsonRowUtils.firstString(this, "day_of_week", "dayOfWeek"),
    timeSlotId = JsonRowUtils.firstString(this, "time_slot_id", "timeSlotId"),
    courseOfferingId = JsonRowUtils.firstString(this, "crs_offering_id", "crsOfferingId"),
    sectionId = JsonRowUtils.firstString(this, "sec_id", "secId", "sectionId"),
    isActive = JsonRowUtils.firstString(this, "is_active", "isActive")
)

fun JsonObject.toSubstitution(): Substitution = Substitution(
    id = JsonRowUtils.firstString(this, "id"),
    classSessionId = JsonRowUtils.firstString(this, "class_session_id", "classSessionId"),
    ttEntryId = JsonRowUtils.firstString(this, "tt_entry_id", "ttEntryId", "timetableEntryId"),
    changeTypeId = JsonRowUtils.firstString(this, "change_type_id", "changeTypeId"),
    oldFacultyId = JsonRowUtils.firstString(this, "old_fac_id", "oldFacId", "originalFacultyId"),
    newFacultyId = JsonRowUtils.firstString(this, "new_fac_id", "newFacId", "substituteFacultyId"),
    oldRoomId = JsonRowUtils.firstString(this, "old_room_id", "oldRoomId"),
    newRoomId = JsonRowUtils.firstString(this, "new_room_id", "newRoomId"),
    oldSessionDate = JsonRowUtils.firstString(this, "old_session_date", "oldSessionDate"),
    newSessionDate = JsonRowUtils.firstString(this, "new_session_date", "newSessionDate", "substitutionDate"),
    reason = JsonRowUtils.firstString(this, "reason"),
    status = JsonRowUtils.firstString(this, "status"),
    remarks = JsonRowUtils.firstString(this, "remarks")
)

fun JsonObject.toTimetableApproval(): TimetableApproval = TimetableApproval(
    id = JsonRowUtils.firstString(this, "id"),
    institutionId = JsonRowUtils.firstString(this, "inst_id", "instId"),
    academicYearId = JsonRowUtils.firstString(this, "academic_year_id", "academicYearId"),
    termId = JsonRowUtils.firstString(this, "term_id", "termId"),
    sectionId = JsonRowUtils.firstString(this, "sec_id", "secId"),
    ttCode = JsonRowUtils.firstString(this, "tt_code", "ttCode"),
    statusId = JsonRowUtils.firstString(this, "status_id", "statusId", "status"),
    description = JsonRowUtils.firstString(this, "description"),
    version = JsonRowUtils.firstString(this, "version", "versionNo", "version_no"),
    remarks = JsonRowUtils.firstString(this, "remarks")
)

fun JsonObject.toTimetableExportResult(): TimetableExportResult = TimetableExportResult(
    exportFormat = JsonRowUtils.firstString(this, "exportFormat", "export_format"),
    mimeType = JsonRowUtils.firstString(this, "mimeType", "mime_type"),
    filename = JsonRowUtils.firstString(this, "filename"),
    recordCount = JsonRowUtils.firstString(this, "recordCount", "record_count")?.toIntOrNull()
)
