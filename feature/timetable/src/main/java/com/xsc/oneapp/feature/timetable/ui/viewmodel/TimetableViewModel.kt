package com.xsc.oneapp.feature.timetable.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xsc.oneapp.core.result.SectionLoader
import com.xsc.oneapp.core.result.UiState
import com.xsc.oneapp.feature.timetable.domain.model.AcademicCalendar
import com.xsc.oneapp.feature.timetable.domain.model.FacultyAllocation
import com.xsc.oneapp.feature.timetable.domain.model.RoomAllocation
import com.xsc.oneapp.feature.timetable.domain.model.Substitution
import com.xsc.oneapp.feature.timetable.domain.model.TimeSlot
import com.xsc.oneapp.feature.timetable.domain.model.TimetableApproval
import com.xsc.oneapp.feature.timetable.domain.model.TimetableEntry
import com.xsc.oneapp.feature.timetable.domain.model.TimetableFilter
import com.xsc.oneapp.feature.timetable.domain.model.TimetablePerspective
import com.xsc.oneapp.feature.timetable.domain.model.WorkingDay
import com.xsc.oneapp.feature.timetable.domain.usecase.CancelSubstitutionUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.DecideTimetableApprovalUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.ExportTimetableUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetAcademicCalendarUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetFacultyAllocationsUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetRoomAllocationsUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetSubstitutionsUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetTimeSlotsUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetTimetableApprovalsUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetTimetableEntriesUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.GetWorkingDaysUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.RequestSubstitutionUseCase
import com.xsc.oneapp.feature.timetable.domain.usecase.SubmitTimetableApprovalUseCase
import com.xsc.sdk.auth.SessionManager
import com.xsc.sdk.network.APIError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** One-shot things the screen has to act on rather than render - a snackbar, same
 * pattern as feature/fee's FeeEffect. */
sealed interface TimetableEffect {
    data class Notify(val message: String, val isError: Boolean = false) : TimetableEffect
}

/**
 * Backs the single unified Timetable screen (spec §17: one screen with the weekly
 * grid, filters, details drawer, allocation info, substitution actions, approval
 * state/actions and export all in the same place, not eight tabs/pages).
 *
 * All eight `view` sections load together in [init] instead of behind a per-tab
 * [SectionLoader.loadOnce] gate. That gate existed to stop a tab-based screen from
 * firing all eight requests for tabs nobody had opened yet (see the previous
 * revision's kdoc) - a real constraint there, since six of those eight tabs might
 * never be opened in a given visit. It doesn't apply here: the redesign puts
 * allocation, substitution and approval information directly on the one screen
 * (drawer, header actions, workload indicator), so all eight are needed for the
 * screen to render correctly on first paint, not deferred work.
 */
@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val getTimetableEntriesUseCase: GetTimetableEntriesUseCase,
    getWorkingDaysUseCase: GetWorkingDaysUseCase,
    getTimeSlotsUseCase: GetTimeSlotsUseCase,
    getAcademicCalendarUseCase: GetAcademicCalendarUseCase,
    getFacultyAllocationsUseCase: GetFacultyAllocationsUseCase,
    getRoomAllocationsUseCase: GetRoomAllocationsUseCase,
    getSubstitutionsUseCase: GetSubstitutionsUseCase,
    getTimetableApprovalsUseCase: GetTimetableApprovalsUseCase,
    private val exportTimetableUseCase: ExportTimetableUseCase,
    private val submitTimetableApprovalUseCase: SubmitTimetableApprovalUseCase,
    private val decideTimetableApprovalUseCase: DecideTimetableApprovalUseCase,
    private val requestSubstitutionUseCase: RequestSubstitutionUseCase,
    private val cancelSubstitutionUseCase: CancelSubstitutionUseCase,
    val sessionManager: SessionManager
) : ViewModel() {

    private val _filter = MutableStateFlow(TimetableFilter())
    val filter: StateFlow<TimetableFilter> = _filter.asStateFlow()

    private val _perspective = MutableStateFlow(TimetablePerspective.SECTION)
    val perspective: StateFlow<TimetablePerspective> = _perspective.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedEntry = MutableStateFlow<TimetableEntry?>(null)
    val selectedEntry: StateFlow<TimetableEntry?> = _selectedEntry.asStateFlow()

    // Union of every entry ever returned by `timetable:view`, keyed by id, kept
    // separately from `entriesState` (which reflects only the *current* filter).
    // The filter bar's Section/Faculty/Room/Term/Year option lists are built from
    // this instead, so narrowing the filter doesn't also shrink the choices offered
    // for widening it back out again.
    private val _knownEntries = MutableStateFlow<List<TimetableEntry>>(emptyList())
    val knownEntries: StateFlow<List<TimetableEntry>> = _knownEntries.asStateFlow()

    // Reads _filter.value at call time (SectionLoader.reload() launches the fetch
    // fresh each time), so updateFilter()'s reload() below always sees the filter
    // just written to it, not the value captured when this lambda was built.
    private val entries = SectionLoader(viewModelScope) { getTimetableEntriesUseCase(_filter.value) }
    private val workingDays = SectionLoader(viewModelScope) { getWorkingDaysUseCase() }
    private val timeSlots = SectionLoader(viewModelScope) { getTimeSlotsUseCase() }
    private val academicCalendar = SectionLoader(viewModelScope) { getAcademicCalendarUseCase() }
    private val facultyAllocations = SectionLoader(viewModelScope) { getFacultyAllocationsUseCase() }
    private val roomAllocations = SectionLoader(viewModelScope) { getRoomAllocationsUseCase() }
    private val substitutions = SectionLoader(viewModelScope) { getSubstitutionsUseCase() }
    private val approvals = SectionLoader(viewModelScope) { getTimetableApprovalsUseCase() }

    val entriesState: StateFlow<UiState<List<TimetableEntry>>> = entries.state
    val workingDaysState: StateFlow<UiState<List<WorkingDay>>> = workingDays.state
    val timeSlotsState: StateFlow<UiState<List<TimeSlot>>> = timeSlots.state
    val academicCalendarState: StateFlow<UiState<AcademicCalendar?>> = academicCalendar.state
    val facultyAllocationsState: StateFlow<UiState<List<FacultyAllocation>>> = facultyAllocations.state
    val roomAllocationsState: StateFlow<UiState<List<RoomAllocation>>> = roomAllocations.state
    val substitutionsState: StateFlow<UiState<List<Substitution>>> = substitutions.state
    val approvalsState: StateFlow<UiState<List<TimetableApproval>>> = approvals.state

    /** RBAC is permission-driven (see TimetableNotes.kt / SessionManager.hasPermission) -
     * the screen reads this to show/hide actions, never `sessionManager.currentRole`. */
    val permissions: StateFlow<List<String>> = sessionManager.currentPermissions

    private val _substitutionInFlight = MutableStateFlow(false)
    val substitutionInFlight: StateFlow<Boolean> = _substitutionInFlight.asStateFlow()

    private val _approvalActionInFlight = MutableStateFlow(false)
    val approvalActionInFlight: StateFlow<Boolean> = _approvalActionInFlight.asStateFlow()

    private val _exportInFlight = MutableStateFlow(false)
    val exportInFlight: StateFlow<Boolean> = _exportInFlight.asStateFlow()

    private val _effects = Channel<TimetableEffect>(Channel.BUFFERED)
    val effects: Flow<TimetableEffect> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            entries.state.collect { state ->
                if (state is UiState.Success) {
                    _knownEntries.value = (_knownEntries.value + state.data)
                        .distinctBy { it.id ?: it.hashCode().toString() }
                }
            }
        }

        entries.loadOnce()
        workingDays.loadOnce()
        timeSlots.loadOnce()
        academicCalendar.loadOnce()
        facultyAllocations.loadOnce()
        roomAllocations.loadOnce()
        substitutions.loadOnce()
        approvals.loadOnce()
    }

    fun retryEntries() = entries.reload()
    fun retryWorkingDays() = workingDays.reload()
    fun retryTimeSlots() = timeSlots.reload()
    fun retryAcademicCalendar() = academicCalendar.reload()
    fun retryFacultyAllocations() = facultyAllocations.reload()
    fun retryRoomAllocations() = roomAllocations.reload()
    fun retrySubstitutions() = substitutions.reload()
    fun retryApprovals() = approvals.reload()

    /** Pull-to-refresh / header "More actions -> Refresh" - every section at once. */
    fun refreshAll() {
        entries.reload()
        workingDays.reload()
        timeSlots.reload()
        academicCalendar.reload()
        facultyAllocations.reload()
        roomAllocations.reload()
        substitutions.reload()
        approvals.reload()
    }

    /** Applies a filter change and refetches the grid - [transform] receives the
     * current filter so the filter bar can flip one field without clobbering the
     * others (e.g. picking a faculty shouldn't reset an already-chosen term). */
    fun updateFilter(transform: (TimetableFilter) -> TimetableFilter) {
        _filter.value = transform(_filter.value)
        entries.reload()
    }

    fun clearFilters() {
        if (_filter.value == TimetableFilter()) return
        _filter.value = TimetableFilter()
        entries.reload()
    }

    /** Section | Faculty | Room (spec §4) - re-emphasizes the grid's existing data,
     * it does not refetch (see [TimetablePerspective]'s kdoc). */
    fun setPerspective(value: TimetablePerspective) {
        _perspective.value = value
    }

    /** Client-side text search over already-loaded entries (spec §1 header search) -
     * there is no server-side search parameter in the contract's view filters
     * (§4.1), and the raw IDs this module returns (see TimetableNotes.kt) are what
     * there is to match against: course offering id, faculty id, room id, session
     * type, and the timetable code. */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectEntry(entry: TimetableEntry?) {
        _selectedEntry.value = entry
    }

    /** spec §2 export action - pdf/excel/csv (contract v2 §4.1 `exportFormat`). See
     * [com.xsc.oneapp.feature.timetable.domain.model.TimetableExportResult] for why
     * this only confirms the export ran rather than opening a file. */
    fun export(format: String) {
        if (_exportInFlight.value) return
        _exportInFlight.value = true
        viewModelScope.launch {
            try {
                val result = exportTimetableUseCase(format, _filter.value)
                val count = result.recordCount?.let { " ($it entries)" } ?: ""
                _effects.send(
                    TimetableEffect.Notify("Exported ${result.filename ?: "timetable"}$count.")
                )
            } catch (e: APIError) {
                _effects.send(TimetableEffect.Notify(e.message ?: "Could not export the timetable.", isError = true))
            } catch (e: Exception) {
                _effects.send(TimetableEffect.Notify(e.message ?: "Could not export the timetable.", isError = true))
            } finally {
                _exportInFlight.value = false
            }
        }
    }

    /** spec §9 - Draft -> Submit for approval. */
    fun submitForApproval(timetableId: String, remarks: String?) {
        if (_approvalActionInFlight.value) return
        _approvalActionInFlight.value = true
        viewModelScope.launch {
            try {
                submitTimetableApprovalUseCase(timetableId, remarks)
                approvals.reload()
                entries.reload()
                _effects.send(TimetableEffect.Notify("Submitted for approval."))
            } catch (e: APIError) {
                _effects.send(TimetableEffect.Notify(e.message ?: "Could not submit for approval.", isError = true))
            } catch (e: Exception) {
                _effects.send(TimetableEffect.Notify(e.message ?: "Could not submit for approval.", isError = true))
            } finally {
                _approvalActionInFlight.value = false
            }
        }
    }

    /** spec §9 - Pending approval -> Approve/Reject. A reject returns the timetable
     * to DRAFT per the contract's documented lifecycle (§7.1). */
    fun decideApproval(timetableId: String, approve: Boolean, remarks: String?) {
        if (_approvalActionInFlight.value) return
        _approvalActionInFlight.value = true
        viewModelScope.launch {
            try {
                decideTimetableApprovalUseCase(timetableId, approve, remarks)
                approvals.reload()
                entries.reload()
                _effects.send(
                    TimetableEffect.Notify(
                        if (approve) "Timetable approved and published." else "Timetable rejected and returned to draft."
                    )
                )
            } catch (e: APIError) {
                _effects.send(TimetableEffect.Notify(e.message ?: "Could not record the decision.", isError = true))
            } catch (e: Exception) {
                _effects.send(TimetableEffect.Notify(e.message ?: "Could not record the decision.", isError = true))
            } finally {
                _approvalActionInFlight.value = false
            }
        }
    }

    /** spec §8 - "Substitution" action from the details drawer. */
    fun requestSubstitution(
        timetableEntryId: String,
        substituteFacultyId: String,
        reason: String,
        originalFacultyId: String?,
        substitutionDate: String?,
        remarks: String?
    ) {
        if (_substitutionInFlight.value) return
        _substitutionInFlight.value = true
        viewModelScope.launch {
            try {
                requestSubstitutionUseCase(
                    timetableEntryId, substituteFacultyId, reason, originalFacultyId, substitutionDate, remarks
                )
                substitutions.reload()
                _effects.send(TimetableEffect.Notify("Substitution recorded."))
            } catch (e: APIError) {
                _effects.send(TimetableEffect.Notify(e.message ?: "Could not record the substitution.", isError = true))
            } catch (e: Exception) {
                _effects.send(TimetableEffect.Notify(e.message ?: "Could not record the substitution.", isError = true))
            } finally {
                _substitutionInFlight.value = false
            }
        }
    }

    fun cancelSubstitution(id: String, reason: String) {
        if (_substitutionInFlight.value) return
        _substitutionInFlight.value = true
        viewModelScope.launch {
            try {
                cancelSubstitutionUseCase(id, reason)
                substitutions.reload()
                _effects.send(TimetableEffect.Notify("Substitution cancelled."))
            } catch (e: APIError) {
                _effects.send(TimetableEffect.Notify(e.message ?: "Could not cancel the substitution.", isError = true))
            } catch (e: Exception) {
                _effects.send(TimetableEffect.Notify(e.message ?: "Could not cancel the substitution.", isError = true))
            } finally {
                _substitutionInFlight.value = false
            }
        }
    }
}
