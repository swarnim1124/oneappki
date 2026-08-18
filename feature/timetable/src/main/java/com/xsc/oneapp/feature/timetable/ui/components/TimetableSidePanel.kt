package com.xsc.oneapp.feature.timetable.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xsc.oneapp.feature.timetable.domain.model.AcademicCalendar
import com.xsc.oneapp.feature.timetable.domain.model.FacultyAllocation
import com.xsc.oneapp.feature.timetable.domain.model.RoomAllocation
import com.xsc.oneapp.feature.timetable.domain.model.Substitution
import com.xsc.oneapp.feature.timetable.domain.model.TimeSlot
import com.xsc.oneapp.feature.timetable.domain.model.TimetableApproval
import com.xsc.oneapp.feature.timetable.domain.model.TimetableEntry
import com.xsc.oneapp.feature.timetable.domain.model.TimetableHeaderInfo
import com.xsc.oneapp.feature.timetable.domain.model.TimetablePermissions
import com.xsc.oneapp.feature.timetable.domain.model.WorkingDay
import com.xsc.sdk.commonui.record.IconBadge
import com.xsc.sdk.theme.LocalSpacing

/** What the right-side panel (desktop/tablet) or bottom sheet (mobile) is currently
 * showing - spec §3 (details drawer) plus §5/§6/§9 (allocation & approval info that
 * has to live on the same screen, not a separate page). */
sealed interface TimetablePanelContent {
    data object Overview : TimetablePanelContent
    data class CellPicker(val entries: List<TimetableEntry>) : TimetablePanelContent
    data class EntryDetail(val entry: TimetableEntry) : TimetablePanelContent
}

@Composable
fun TimetablePanelHost(
    content: TimetablePanelContent,
    onBack: () -> Unit,
    onSelectEntry: (TimetableEntry) -> Unit,
    workingDays: List<WorkingDay>,
    timeSlots: List<TimeSlot>,
    substitutions: List<Substitution>,
    facultyAllocations: List<FacultyAllocation>,
    roomAllocations: List<RoomAllocation>,
    approvals: List<TimetableApproval>,
    academicCalendar: AcademicCalendar?,
    header: TimetableHeaderInfo?,
    permissions: List<String>,
    onSubmitForApproval: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onRequestSubstitution: (TimetableEntry) -> Unit,
    onCancelSubstitution: (Substitution) -> Unit,
    approvalActionInFlight: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (content !is TimetablePanelContent.Overview) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to overview")
                }
                Text(
                    when (content) {
                        is TimetablePanelContent.EntryDetail -> "Class details"
                        is TimetablePanelContent.CellPicker -> "${content.entries.size} classes"
                        TimetablePanelContent.Overview -> ""
                    },
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }

        when (content) {
            TimetablePanelContent.Overview -> OverviewPanel(
                header = header,
                facultyAllocations = facultyAllocations,
                roomAllocations = roomAllocations,
                approvals = approvals,
                academicCalendar = academicCalendar,
                permissions = permissions,
                onSubmitForApproval = onSubmitForApproval,
                onApprove = onApprove,
                onReject = onReject,
                approvalActionInFlight = approvalActionInFlight
            )

            is TimetablePanelContent.CellPicker -> CellPickerPanel(
                entries = content.entries,
                timeSlots = timeSlots,
                onSelect = onSelectEntry
            )

            is TimetablePanelContent.EntryDetail -> EntryDetailPanel(
                entry = content.entry,
                workingDays = workingDays,
                timeSlots = timeSlots,
                substitutions = substitutions,
                permissions = permissions,
                onRequestSubstitution = { onRequestSubstitution(content.entry) },
                onCancelSubstitution = onCancelSubstitution
            )
        }
    }
}

// --- Overview: approval workflow + faculty/room allocation summaries ---

@Composable
private fun OverviewPanel(
    header: TimetableHeaderInfo?,
    facultyAllocations: List<FacultyAllocation>,
    roomAllocations: List<RoomAllocation>,
    approvals: List<TimetableApproval>,
    academicCalendar: AcademicCalendar?,
    permissions: List<String>,
    onSubmitForApproval: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    approvalActionInFlight: Boolean
) {
    val spacing = LocalSpacing.current
    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(spacing.lg),
        verticalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        item {
            ApprovalWorkflowCard(
                header = header,
                approvals = approvals,
                permissions = permissions,
                onSubmitForApproval = onSubmitForApproval,
                onApprove = onApprove,
                onReject = onReject,
                inFlight = approvalActionInFlight
            )
        }

        if (academicCalendar?.adjustments?.isNotEmpty() == true) {
            item { SectionLabel("Calendar adjustments") }
            items(academicCalendar.adjustments) { adj ->
                PanelCard {
                    Text(
                        adj.holidayDate?.let { "Holiday: $it" } ?: "Adjustment",
                        style = MaterialTheme.typography.titleSmall
                    )
                    adj.adjustmentTypeId?.let {
                        DetailLine("Type", it.replace('_', ' ').replaceFirstChar(Char::uppercase))
                    }
                    if (adj.sourceWorkingDayId != null || adj.targetWorkingDayId != null || adj.targetDate != null) {
                        DetailLine(
                            "Shift",
                            listOfNotNull(
                                adj.sourceWorkingDayId?.let { "from day $it" },
                                (adj.targetWorkingDayId ?: adj.targetDate)?.let { "to $it" }
                            ).joinToString(" ")
                        )
                    }
                    adj.remarks?.let { DetailLine("Remarks", it) }
                }
            }
        }

        item { SectionLabel("Faculty allocation") }
        if (facultyAllocations.isEmpty()) {
            item {
                Text(
                    "No faculty allocations published yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(facultyAllocations, key = { it.hashCode() }) { allocation ->
                PanelCard {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Faculty #${allocation.facultyId ?: "—"}", style = MaterialTheme.typography.titleSmall)
                        allocation.weeklyHours?.let { WorkloadPill(it) }
                    }
                    val meta = listOfNotNull(
                        allocation.courseOfferingId?.let { "Course #$it" },
                        allocation.sectionId?.let { "Section $it" },
                        allocation.assignmentRoleId
                    ).joinToString(" · ")
                    if (meta.isNotBlank()) {
                        Text(meta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        item { SectionLabel("Room allocation") }
        if (roomAllocations.isEmpty()) {
            item {
                Text(
                    "No room allocations published yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(roomAllocations, key = { it.hashCode() }) { allocation ->
                PanelCard {
                    Text("Room #${allocation.roomId ?: "—"}", style = MaterialTheme.typography.titleSmall)
                    val meta = listOfNotNull(
                        allocation.dayOfWeek,
                        allocation.timeSlotId?.let { "Slot $it" },
                        allocation.courseOfferingId?.let { "Course #$it" }
                    ).joinToString(" · ")
                    if (meta.isNotBlank()) {
                        Text(meta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkloadPill(weeklyHours: String) {
    val hours = weeklyHours.toDoubleOrNull()
    // Restrained workload indicator (spec §5) - a plain number past a soft 18h/week
    // threshold reads as heavier loaded; no institution-specific cap is documented,
    // so this is visual emphasis only, never a hard limit enforced client side.
    val heavy = hours != null && hours >= 18.0
    Text(
        "$weeklyHours h/wk",
        style = MaterialTheme.typography.labelSmall,
        color = if (heavy) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun ApprovalWorkflowCard(
    header: TimetableHeaderInfo?,
    approvals: List<TimetableApproval>,
    permissions: List<String>,
    onSubmitForApproval: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    inFlight: Boolean
) {
    val status = header?.status ?: approvals.firstOrNull()?.statusId
    val canSubmit = permissions.contains(TimetablePermissions.TIMETABLE_APPROVAL_ADD)
    val canDecide = permissions.contains(TimetablePermissions.TIMETABLE_APPROVAL_UPDATE)

    PanelCard {
        Text("Approval workflow", style = MaterialTheme.typography.titleSmall)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WorkflowStep("Draft", status == null || status.equals("DRAFT", true))
            StepArrow()
            WorkflowStep("Pending", status.equals("PENDING_APPROVAL", true))
            StepArrow()
            WorkflowStep("Published", status.equals("PUBLISHED", true))
        }

        val rejected = approvals.firstOrNull { it.statusId.equals("REJECTED", ignoreCase = true) || it.statusId.equals("REJECT", ignoreCase = true) }
        if (rejected != null && status.equals("DRAFT", ignoreCase = true)) {
            Text(
                "Last submission was rejected" + (rejected.remarks ?: rejected.description)?.let { ": $it" }.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        val timetableId = header?.timetableId
        Row(modifier = Modifier.fillMaxWidth().padding(top = spacingTop), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            when {
                timetableId == null -> Unit
                status.equals("DRAFT", ignoreCase = true) || status == null -> if (canSubmit) {
                    Button(onClick = onSubmitForApproval, enabled = !inFlight) { Text("Submit for approval") }
                }
                status.equals("PENDING_APPROVAL", ignoreCase = true) -> if (canDecide) {
                    OutlinedButton(onClick = onReject, enabled = !inFlight) { Text("Reject") }
                    Button(onClick = onApprove, enabled = !inFlight) { Text("Approve") }
                }
                else -> Unit // PUBLISHED: view/export only, spec §9 - not directly editable
            }
        }
    }
}

private val spacingTop = 10.dp

@Composable
private fun WorkflowStep(label: String, active: Boolean) {
    Text(
        label,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
        color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun StepArrow() {
    Text("→", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
}

// --- Cell picker: several classes share one day+slot (different sections/rooms) ---

@Composable
private fun CellPickerPanel(
    entries: List<TimetableEntry>,
    timeSlots: List<TimeSlot>,
    onSelect: (TimetableEntry) -> Unit
) {
    val spacing = LocalSpacing.current
    LazyColumn(contentPadding = androidx.compose.foundation.layout.PaddingValues(spacing.lg), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        items(entries, key = { it.id ?: it.hashCode().toString() }) { entry ->
            Card(onClick = { onSelect(entry) }, modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(0.dp)) {
                Column(modifier = Modifier.padding(spacing.md)) {
                    Text(entry.courseLabel(), style = MaterialTheme.typography.titleSmall)
                    Text(
                        "${entry.facultyLabel()} · ${entry.roomLabel()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// --- Entry detail (spec §3) ---

@Composable
private fun EntryDetailPanel(
    entry: TimetableEntry,
    workingDays: List<WorkingDay>,
    timeSlots: List<TimeSlot>,
    substitutions: List<Substitution>,
    permissions: List<String>,
    onRequestSubstitution: () -> Unit,
    onCancelSubstitution: (Substitution) -> Unit
) {
    val spacing = LocalSpacing.current
    val slot = timeSlots.firstOrNull { it.id == entry.timeSlotId }
    val dayLabel = workingDays.firstOrNull { it.id == entry.workingDayId || it.dayOfWeekId == entry.workingDayId }
        ?.let { it.dayName ?: it.name }
        ?: entry.dayOfWeek?.prettyDay()
    val activeSub = substitutions.firstOrNull {
        it.status?.equals("ACTIVE", ignoreCase = true) == true &&
            (it.ttEntryId == entry.id || it.classSessionId == entry.id)
    }
    val canSubstitute = permissions.contains(TimetablePermissions.SUBSTITUTION_ADD)

    LazyColumn(contentPadding = androidx.compose.foundation.layout.PaddingValues(spacing.lg), verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        item {
            PanelCard {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    IconBadge(Icons.Default.MenuBook)
                    Column {
                        Text(entry.courseLabel(), style = MaterialTheme.typography.titleMedium)
                        entry.sessionTypeId?.let {
                            Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = spacing.md))
                DetailLine("Faculty", entry.facultyLabel())
                DetailLine("Room", entry.roomLabel())
                DetailLine("Day", dayLabel ?: "—")
                DetailLine("Time", slot?.displayRange() ?: (entry.timeSlotId?.let { "Slot #$it" } ?: "—"))
                if (entry.startDate != null || entry.endDate != null) {
                    DetailLine("Active dates", listOfNotNull(entry.startDate, entry.endDate).joinToString(" – "))
                }
                DetailLine("Section", entry.sectionId ?: "—")
                DetailLine("Academic year", entry.academicYearId ?: "—")
                DetailLine("Term", entry.termId ?: "—")
                DetailLine("Active", if (entry.isActive.isTruthy()) "Yes" else "No")
            }
        }

        item {
            if (activeSub != null) {
                PanelCard {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.padding(end = 2.dp))
                        Text("Active substitution", style = MaterialTheme.typography.titleSmall)
                    }
                    DetailLine("Original faculty", activeSub.oldFacultyId?.let { "Faculty #$it" } ?: "—")
                    DetailLine("Substitute faculty", activeSub.newFacultyId?.let { "Faculty #$it" } ?: "—")
                    DetailLine("Date", activeSub.newSessionDate ?: "—")
                    DetailLine("Reason", activeSub.reason ?: "—")
                    activeSub.remarks?.let { DetailLine("Remarks", it) }
                    DetailLine("Status", activeSub.status ?: "—")
                    if (canSubstitute) {
                        OutlinedButton(
                            onClick = { onCancelSubstitution(activeSub) },
                            modifier = Modifier.padding(top = spacing.sm)
                        ) { Text("Cancel substitution") }
                    }
                }
            } else if (canSubstitute) {
                PanelCard {
                    Text("No active substitution", style = MaterialTheme.typography.titleSmall)
                    Button(onClick = onRequestSubstitution, modifier = Modifier.padding(top = spacing.sm)) {
                        Text("Substitution")
                    }
                }
            }
        }

        item { SectionLabel("Technical details") }
        item {
            PanelCard {
                DetailLine("Timetable entry ID", entry.id ?: "—")
                DetailLine("Timetable ID", entry.ttId ?: "—")
                DetailLine("Timetable code", entry.ttCode ?: "—")
                DetailLine("Course offering ID", entry.courseOfferingId ?: "—")
                DetailLine("Faculty/course assignment ID", entry.facultyCourseAssignmentId ?: "—")
                DetailLine("Working day ID", entry.workingDayId ?: "—")
                DetailLine("Time slot ID", entry.timeSlotId ?: "—")
                DetailLine("Room ID", entry.roomId ?: "—")
            }
        }
    }
}

// --- Shared small pieces ---

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
private fun PanelCard(content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    val spacing = LocalSpacing.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(spacing.lg), content = content)
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}
