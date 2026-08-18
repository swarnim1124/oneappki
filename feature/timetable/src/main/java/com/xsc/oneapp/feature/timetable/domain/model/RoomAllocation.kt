package com.xsc.oneapp.feature.timetable.domain.model

/** Timetable entries that have a room assigned - a filtered view over
 * tb_tt_entry, not its own table (see TimetableEntry).
 *
 * [courseOfferingId]/[sectionId] (the course/session using the room) and
 * [isActive] are read defensively the same way [FacultyAllocation]'s extra fields
 * are - contract v2 §5.2 documents the underlying join (`tb_tt_entry`,
 * `sch_infra.tb_room`, `tb_class_session_room`) but not a fixed `view` response
 * shape, so these populate when a backend returns them and stay null otherwise. */
data class RoomAllocation(
    val id: String?,
    val ttId: String?,
    val roomId: String?,
    val dayOfWeek: String?,
    val timeSlotId: String?,
    val courseOfferingId: String? = null,
    val sectionId: String? = null,
    val isActive: String? = null
)
