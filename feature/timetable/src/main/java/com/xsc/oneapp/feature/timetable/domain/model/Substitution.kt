package com.xsc.oneapp.feature.timetable.domain.model

/** sch_timetable.tb_class_session_change - a session-level substitution
 * (faculty/room/date swap) against a [TimetableEntry].
 *
 * [remarks] is contract v2 §6.1's `add` payload's optional field, distinct from
 * [reason] (required) - kept separate rather than merged since the UI (spec §8)
 * shows them as two lines. */
data class Substitution(
    val id: String?,
    val classSessionId: String?,
    val ttEntryId: String?,
    val changeTypeId: String?,
    val oldFacultyId: String?,
    val newFacultyId: String?,
    val oldRoomId: String?,
    val newRoomId: String?,
    val oldSessionDate: String?,
    val newSessionDate: String?,
    val reason: String?,
    val status: String?,
    val remarks: String? = null
)
