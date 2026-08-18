@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.xsc.oneapp.feature.timetable.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xsc.oneapp.core.result.UiState
import com.xsc.oneapp.core.result.dataOrNull
import com.xsc.oneapp.feature.timetable.domain.model.Substitution
import com.xsc.oneapp.feature.timetable.domain.model.TimetableEntry
import com.xsc.oneapp.feature.timetable.domain.model.TimetableFilter
import com.xsc.oneapp.feature.timetable.domain.model.TimetableHeaderInfo
import com.xsc.oneapp.feature.timetable.domain.model.TimetablePermissions
import com.xsc.oneapp.feature.timetable.domain.model.WeeklySchedule
import com.xsc.oneapp.feature.timetable.ui.components.ApprovalDecisionDialog
import com.xsc.oneapp.feature.timetable.ui.components.CancelSubstitutionDialog
import com.xsc.oneapp.feature.timetable.ui.components.ExportDialog
import com.xsc.oneapp.feature.timetable.ui.components.StackedDayTimetable
import com.xsc.oneapp.feature.timetable.ui.components.SubmitForApprovalDialog
import com.xsc.oneapp.feature.timetable.ui.components.SubstitutionRequestDialog
import com.xsc.oneapp.feature.timetable.ui.components.TimetableFilterSheet
import com.xsc.oneapp.feature.timetable.ui.components.TimetableGridSkeleton
import com.xsc.oneapp.feature.timetable.ui.components.TimetableHeaderBar
import com.xsc.oneapp.feature.timetable.ui.components.TimetablePanelContent
import com.xsc.oneapp.feature.timetable.ui.components.TimetablePanelHost
import com.xsc.oneapp.feature.timetable.ui.components.WeeklyTimetableGrid
import com.xsc.oneapp.feature.timetable.ui.components.matchesSearch
import com.xsc.oneapp.feature.timetable.ui.viewmodel.TimetableEffect
import com.xsc.oneapp.feature.timetable.ui.viewmodel.TimetableViewModel
import com.xsc.sdk.commonui.record.EmptyState
import com.xsc.sdk.commonui.record.ErrorState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

/** Breakpoints (Material's medium/expanded convention, spec §12): >=840dp gets the
 * grid plus a persistent side panel (desktop). 600-840dp keeps the grid but the
 * panel becomes a bottom sheet (tablet). <600dp switches to the stacked day/period
 * list (phone) - not a shrunk grid, a different composition of the same schedule. */
private val EXPANDED_BREAKPOINT = 840.dp
private val TABLET_BREAKPOINT = 600.dp

/**
 * One unified Timetable screen (spec §17): weekly grid, filters, details drawer,
 * allocation info, substitution actions, approval state/actions and export all in
 * one place - no configuration/allocation/substitution/approval sub-pages.
 */
@Composable
fun TimetableScreen(
    onBack: () -> Unit,
    viewModel: TimetableViewModel = hiltViewModel()
) {
    val entriesState by viewModel.entriesState.collectAsStateWithLifecycle()
    val workingDaysState by viewModel.workingDaysState.collectAsStateWithLifecycle()
    val timeSlotsState by viewModel.timeSlotsState.collectAsStateWithLifecycle()
    val academicCalendarState by viewModel.academicCalendarState.collectAsStateWithLifecycle()
    val facultyAllocationsState by viewModel.facultyAllocationsState.collectAsStateWithLifecycle()
    val roomAllocationsState by viewModel.roomAllocationsState.collectAsStateWithLifecycle()
    val substitutionsState by viewModel.substitutionsState.collectAsStateWithLifecycle()
    val approvalsState by viewModel.approvalsState.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val perspective by viewModel.perspective.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val knownEntries by viewModel.knownEntries.collectAsStateWithLifecycle()
    val permissions by viewModel.permissions.collectAsStateWithLifecycle()
    val substitutionInFlight by viewModel.substitutionInFlight.collectAsStateWithLifecycle()
    val approvalActionInFlight by viewModel.approvalActionInFlight.collectAsStateWithLifecycle()
    val exportInFlight by viewModel.exportInFlight.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is TimetableEffect.Notify -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    var filterSheetOpen by remember { mutableStateOf(false) }
    var exportDialogOpen by remember { mutableStateOf(false) }
    var submitDialogOpen by remember { mutableStateOf(false) }
    var decisionApprove by remember { mutableStateOf<Boolean?>(null) }
    var substitutionTarget by remember { mutableStateOf<TimetableEntry?>(null) }
    var cancelSubTarget by remember { mutableStateOf<Substitution?>(null) }
    var panelContent by remember { mutableStateOf<TimetablePanelContent>(TimetablePanelContent.Overview) }
    var mobilePanelOpen by remember { mutableStateOf(false) }

    val workingDays = workingDaysState.dataOrNull().orEmpty()
    val timeSlots = timeSlotsState.dataOrNull().orEmpty()
    val substitutions = substitutionsState.dataOrNull().orEmpty()
    val facultyAllocations = facultyAllocationsState.dataOrNull().orEmpty()
    val roomAllocations = roomAllocationsState.dataOrNull().orEmpty()
    val approvals = approvalsState.dataOrNull().orEmpty()
    val academicCalendar = academicCalendarState.dataOrNull()

    val header = remember(knownEntries, approvals, filter) {
        TimetableHeaderInfo.from(knownEntries, approvals, filter)
    }

    val onCellClick: (List<TimetableEntry>) -> Unit = { cellEntries ->
        panelContent = when {
            cellEntries.isEmpty() -> TimetablePanelContent.Overview
            cellEntries.size == 1 -> TimetablePanelContent.EntryDetail(cellEntries.first())
            else -> TimetablePanelContent.CellPicker(cellEntries)
        }
        viewModel.selectEntry(cellEntries.singleOrNull())
        mobilePanelOpen = cellEntries.isNotEmpty()
    }
    val onSelectEntry: (TimetableEntry) -> Unit = { entry ->
        panelContent = TimetablePanelContent.EntryDetail(entry)
        viewModel.selectEntry(entry)
    }
    val onPanelBack: () -> Unit = {
        panelContent = TimetablePanelContent.Overview
        viewModel.selectEntry(null)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigate up")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(padding)) {
            val wide = maxWidth >= EXPANDED_BREAKPOINT
            val tablet = maxWidth >= TABLET_BREAKPOINT

            Column(modifier = Modifier.fillMaxSize()) {
                TimetableHeaderBar(
                    header = header,
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::setSearchQuery,
                    activeFilterCount = filter.activeFieldCount(),
                    onFilterClick = { filterSheetOpen = true },
                    canExport = permissions.contains(TimetablePermissions.TIMETABLE_VIEW),
                    onExportClick = { exportDialogOpen = true },
                    onRefreshClick = viewModel::refreshAll,
                    onOverviewClick = {
                        panelContent = TimetablePanelContent.Overview
                        viewModel.selectEntry(null)
                        mobilePanelOpen = true
                    }
                )

                val gridLoading = entriesState is UiState.Loading ||
                    workingDaysState is UiState.Loading ||
                    timeSlotsState is UiState.Loading

                when {
                    gridLoading -> TimetableGridSkeleton(modifier = Modifier.weight(1f).fillMaxWidth())

                    entriesState is UiState.BusinessError ||
                        entriesState is UiState.NetworkError ||
                        entriesState is UiState.UnexpectedError -> {
                        val message = when (val s = entriesState) {
                            is UiState.BusinessError -> s.message
                            is UiState.NetworkError -> s.message
                            is UiState.UnexpectedError -> s.message
                            else -> "Something went wrong."
                        }
                        ErrorState(message, onRetry = viewModel::retryEntries, modifier = Modifier.weight(1f).fillMaxWidth())
                    }

                    else -> {
                        val entries = (entriesState as? UiState.Success)?.data.orEmpty()
                        val visibleEntries = remember(entries, searchQuery) {
                            if (searchQuery.isBlank()) entries else entries.filter { it.matchesSearch(searchQuery) }
                        }

                        if (visibleEntries.isEmpty()) {
                            EmptyState(
                                if (filter.isActive || searchQuery.isNotBlank()) {
                                    "No classes match the selected filters."
                                } else {
                                    "No timetable available for this selection."
                                },
                                modifier = Modifier.weight(1f).fillMaxWidth()
                            )
                        } else {
                            val today = remember { LocalDate.now().dayOfWeek.name }
                            val schedule = remember(visibleEntries, workingDays, timeSlots, today) {
                                WeeklySchedule.build(visibleEntries, workingDays, timeSlots, today)
                            }

                            if (schedule.isEmpty) {
                                EmptyState("No timetable available for this selection.", modifier = Modifier.weight(1f).fillMaxWidth())
                            } else {
                                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                        if (tablet) {
                                            WeeklyTimetableGrid(schedule, substitutions, onCellClick)
                                        } else {
                                            StackedDayTimetable(schedule, substitutions, onCellClick)
                                        }
                                    }

                                    if (wide) {
                                        Box(
                                            modifier = Modifier
                                                .width(1.dp)
                                                .fillMaxHeight()
                                                .background(MaterialTheme.colorScheme.outlineVariant)
                                        )
                                        Box(modifier = Modifier.width(380.dp).fillMaxHeight()) {
                                            TimetablePanelHost(
                                                content = panelContent,
                                                onBack = onPanelBack,
                                                onSelectEntry = onSelectEntry,
                                                workingDays = workingDays,
                                                timeSlots = timeSlots,
                                                substitutions = substitutions,
                                                facultyAllocations = facultyAllocations,
                                                roomAllocations = roomAllocations,
                                                approvals = approvals,
                                                academicCalendar = academicCalendar,
                                                header = header,
                                                permissions = permissions,
                                                onSubmitForApproval = { submitDialogOpen = true },
                                                onApprove = { decisionApprove = true },
                                                onReject = { decisionApprove = false },
                                                onRequestSubstitution = { entry -> substitutionTarget = entry },
                                                onCancelSubstitution = { sub -> cancelSubTarget = sub },
                                                approvalActionInFlight = approvalActionInFlight
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (filterSheetOpen) {
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
                val scope = rememberCoroutineScope()
                ModalBottomSheet(onDismissRequest = { filterSheetOpen = false }, sheetState = sheetState) {
                    val sectionOptions = remember(knownEntries) { knownEntries.mapNotNull { it.sectionId }.distinct().sorted() }
                    val facultyOptions = remember(knownEntries, facultyAllocations) {
                        (knownEntries.mapNotNull { it.facultyId } + facultyAllocations.mapNotNull { it.facultyId }).distinct().sorted()
                    }
                    val roomOptions = remember(knownEntries, roomAllocations) {
                        (knownEntries.mapNotNull { it.roomId } + roomAllocations.mapNotNull { it.roomId }).distinct().sorted()
                    }
                    val termOptions = remember(knownEntries) { knownEntries.mapNotNull { it.termId }.distinct().sorted() }
                    val yearOptions = remember(knownEntries) { knownEntries.mapNotNull { it.academicYearId }.distinct().sorted() }
                    val ttIdOptions = remember(knownEntries, approvals) {
                        (knownEntries.mapNotNull { it.ttId } + approvals.mapNotNull { it.id }).distinct().sorted()
                    }

                    TimetableFilterSheet(
                        filter = filter,
                        perspective = perspective,
                        onPerspectiveChange = viewModel::setPerspective,
                        workingDays = workingDays,
                        timeSlots = timeSlots,
                        sectionOptions = sectionOptions,
                        facultyOptions = facultyOptions,
                        roomOptions = roomOptions,
                        termOptions = termOptions,
                        yearOptions = yearOptions,
                        ttIdOptions = ttIdOptions,
                        onFilterChange = { updated -> viewModel.updateFilter { updated } },
                        onClear = viewModel::clearFilters,
                        onDone = { scope.launch { sheetState.hide() }.invokeOnCompletion { filterSheetOpen = false } }
                    )
                }
            }

            if (!wide && mobilePanelOpen) {
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ModalBottomSheet(
                    onDismissRequest = { mobilePanelOpen = false; onPanelBack() },
                    sheetState = sheetState
                ) {
                    TimetablePanelHost(
                        content = panelContent,
                        onBack = onPanelBack,
                        onSelectEntry = onSelectEntry,
                        workingDays = workingDays,
                        timeSlots = timeSlots,
                        substitutions = substitutions,
                        facultyAllocations = facultyAllocations,
                        roomAllocations = roomAllocations,
                        approvals = approvals,
                        academicCalendar = academicCalendar,
                        header = header,
                        permissions = permissions,
                        onSubmitForApproval = { submitDialogOpen = true },
                        onApprove = { decisionApprove = true },
                        onReject = { decisionApprove = false },
                        onRequestSubstitution = { entry -> substitutionTarget = entry },
                        onCancelSubstitution = { sub -> cancelSubTarget = sub },
                        approvalActionInFlight = approvalActionInFlight,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (exportDialogOpen) {
                ExportDialog(
                    inFlight = exportInFlight,
                    onDismiss = { exportDialogOpen = false },
                    onExport = { format -> viewModel.export(format); exportDialogOpen = false }
                )
            }

            if (submitDialogOpen) {
                SubmitForApprovalDialog(
                    inFlight = approvalActionInFlight,
                    onDismiss = { submitDialogOpen = false },
                    onSubmit = { remarks ->
                        header?.timetableId?.let { viewModel.submitForApproval(it, remarks) }
                        submitDialogOpen = false
                    }
                )
            }

            decisionApprove?.let { approve ->
                ApprovalDecisionDialog(
                    approve = approve,
                    inFlight = approvalActionInFlight,
                    onDismiss = { decisionApprove = null },
                    onConfirm = { remarks ->
                        header?.timetableId?.let { viewModel.decideApproval(it, approve, remarks) }
                        decisionApprove = null
                    }
                )
            }

            substitutionTarget?.let { entry ->
                SubstitutionRequestDialog(
                    originalFacultyId = entry.facultyId,
                    inFlight = substitutionInFlight,
                    onDismiss = { substitutionTarget = null },
                    onSubmit = { substituteFacultyId, reason, date, remarks ->
                        entry.id?.let {
                            viewModel.requestSubstitution(it, substituteFacultyId, reason, entry.facultyId, date, remarks)
                        }
                        substitutionTarget = null
                    }
                )
            }

            cancelSubTarget?.let { sub ->
                CancelSubstitutionDialog(
                    inFlight = substitutionInFlight,
                    onDismiss = { cancelSubTarget = null },
                    onConfirm = { reason ->
                        sub.id?.let { viewModel.cancelSubstitution(it, reason) }
                        cancelSubTarget = null
                    }
                )
            }
        }
    }
}

private fun TimetableFilter.activeFieldCount(): Int =
    listOfNotNull(ttId, sectionId, facultyId, roomId, dayOfWeek, timeSlotId, termId, academicYearId).size
