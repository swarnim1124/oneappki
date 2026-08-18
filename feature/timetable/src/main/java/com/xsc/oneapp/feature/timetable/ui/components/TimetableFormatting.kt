package com.xsc.oneapp.feature.timetable.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.xsc.oneapp.feature.timetable.domain.model.TimeSlot
import com.xsc.oneapp.feature.timetable.domain.model.TimetableEntry
import com.xsc.sdk.theme.LocalStatusColors

/** Shared, small formatting helpers used across the timetable grid, header, filter
 * bar and details drawer - kept in one place so a label doesn't drift between them. */

/** "09:00:00" -> "09:00" - strips the seconds the backend always sends. */
fun TimeSlot.displayRange(): String? {
    val start = startTime?.take(5)
    val end = endTime?.take(5)
    return listOfNotNull(start, end).takeIf { it.isNotEmpty() }?.joinToString(" – ")
}

val TimeSlot.isBreakSlot: Boolean
    get() = isBreak.isTruthy()

fun String?.isTruthy(): Boolean =
    this != null && (equals("true", ignoreCase = true) || this == "1")

/** m_timetable resolves none of its foreign keys to names - there is no course or
 * room master-list call in this module (see TimetableNotes.kt), so the honest label
 * is the id with enough of a prefix that it reads as a reference rather than a
 * mystery number. */
fun TimetableEntry.courseLabel(): String {
    val id = courseOfferingId ?: courseId
    return if (id != null) "Course #$id" else "Class"
}

fun TimetableEntry.facultyLabel(): String = facultyId?.let { "Faculty #$it" } ?: "Faculty —"

fun TimetableEntry.roomLabel(): String = roomId?.let { "Room #$it" } ?: "Room —"

/** Client-side text search (see TimetableViewModel.setSearchQuery's kdoc for why
 * there's no server-side search parameter to use instead). */
fun TimetableEntry.matchesSearch(query: String): Boolean {
    if (query.isBlank()) return true
    val q = query.trim()
    return listOfNotNull(
        courseOfferingId, courseId, facultyId, roomId, sessionTypeId, ttCode, sectionId
    ).any { it.contains(q, ignoreCase = true) }
}

fun String.prettyDay(): String = lowercase().replaceFirstChar { it.uppercase() }

/** Session type / status pill tone - restrained, semantic-only per spec §13 (no
 * rainbow cells). */
@Composable
@ReadOnlyComposable
fun timetableStatusColor(status: String?): Color = when (status?.uppercase()) {
    "PUBLISHED", "ACTIVE", "APPROVE", "APPROVED" -> LocalStatusColors.current.success
    "PENDING_APPROVAL", "PENDING" -> LocalStatusColors.current.warning
    "DRAFT" -> MaterialTheme.colorScheme.onSurfaceVariant
    "REJECT", "REJECTED", "CANCELLED" -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.primary
}

fun String?.statusLabel(): String = when (this?.uppercase()) {
    "PENDING_APPROVAL" -> "Pending approval"
    null -> "—"
    else -> lowercase().split('_', ' ').joinToString(" ") { it.replaceFirstChar(Char::uppercase) }
}
