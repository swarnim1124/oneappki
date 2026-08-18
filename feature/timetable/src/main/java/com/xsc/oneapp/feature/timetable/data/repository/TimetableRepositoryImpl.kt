package com.xsc.oneapp.feature.timetable.data.repository

import com.google.gson.JsonElement
import com.xsc.oneapp.core.json.JsonRowUtils
import com.xsc.oneapp.feature.timetable.data.mapper.toAcademicCalendar
import com.xsc.oneapp.feature.timetable.data.mapper.toFacultyAllocation
import com.xsc.oneapp.feature.timetable.data.mapper.toRoomAllocation
import com.xsc.oneapp.feature.timetable.data.mapper.toSubstitution
import com.xsc.oneapp.feature.timetable.data.mapper.toTimeSlot
import com.xsc.oneapp.feature.timetable.data.mapper.toTimetableApproval
import com.xsc.oneapp.feature.timetable.data.mapper.toTimetableEntry
import com.xsc.oneapp.feature.timetable.data.mapper.toTimetableExportResult
import com.xsc.oneapp.feature.timetable.data.mapper.toWorkingDay
import com.xsc.oneapp.feature.timetable.data.network.TimetableEndpoint
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
import com.xsc.oneapp.feature.timetable.domain.repository.TimetableRepository
import com.xsc.sdk.auth.SessionManager
import com.xsc.sdk.network.APIClient
import com.xsc.sdk.network.APIError
import javax.inject.Inject

/** No request-payload example was confirmed for the 8 view calls beyond `inst_id` -
 * only the response shapes were (see class-level note below and TimetableNotes.kt).
 * `timetable:view`'s optional filters (ttId/sectionId/facultyId/roomId/dayOfWeek/
 * timeSlotId/termId/academicYearId/page/limit/exportFormat) and the mutation calls
 * (submit/approve/reject approval, request/cancel substitution) *are* contract-
 * documented with full request examples (contract v2 §4.1, §6.1, §7.1), so those are
 * sent as named, contract-cased keys rather than the defensive multi-key reads this
 * class uses for response parsing.
 *
 * Confirmed *not* optional, 2026-08-16: this module's own gateway rejects every one
 * of these 8 view calls with "INST_ID is required" when it's missing, so unlike
 * m_fees this is no longer sent defensively-if-present - see [instIdPayload]. */
class TimetableRepositoryImpl @Inject constructor(
    private val apiClient: APIClient,
    private val sessionManager: SessionManager
) : TimetableRepository {

    /**
     * `inst_id` is genuinely required here (see class kdoc), so unlike m_fees's
     * identically-shaped `instPayload()` this fails fast with a clear, actionable
     * message instead of silently sending the request without it and letting the
     * backend's terser "INST_ID is required" reach the user instead.
     *
     * [SessionManager.getInstitutionId] is normally set from the login response
     * (see TokenManager.institutionId) with a JWT-claim fallback for sessions that
     * predate that field or a login response that omitted it. If both are still
     * empty here, the session genuinely doesn't have one on file - it is not
     * something a retry on this same screen can fix.
     */
    private fun instIdPayload(): MutableMap<String, Any> {
        val institutionId = sessionManager.getInstitutionId()
            ?: throw APIError.BusinessError(
                "INST_ID_MISSING",
                "Your session is missing institution info. Please log out and log back in."
            )
        return mutableMapOf("inst_id" to institutionId)
    }

    /** [instIdPayload] plus [filter]'s non-null fields under the contract's own
     * camelCase key names (contract v2 §4.1's request example uses `sectionId`,
     * not `sec_id`, for the view filters - unlike the response rows, which are raw
     * snake_case ORM columns). */
    private fun filterPayload(filter: TimetableFilter): MutableMap<String, Any> {
        val payload = instIdPayload()
        filter.ttId?.let { payload["ttId"] = it }
        filter.sectionId?.let { payload["sectionId"] = it }
        filter.facultyId?.let { payload["facultyId"] = it }
        filter.roomId?.let { payload["roomId"] = it }
        filter.dayOfWeek?.let { payload["dayOfWeek"] = it }
        filter.timeSlotId?.let { payload["timeSlotId"] = it }
        filter.termId?.let { payload["termId"] = it }
        filter.academicYearId?.let { payload["academicYearId"] = it }
        filter.page?.let { payload["page"] = it }
        filter.limit?.let { payload["limit"] = it }
        return payload
    }

    /** m_timetable contract: sm_schedule/timetable/view. */
    override suspend fun getTimetableEntries(filter: TimetableFilter): List<TimetableEntry> {
        val data = apiClient.request<JsonElement?>(
            module = TimetableEndpoint.MODULE,
            submodule = TimetableEndpoint.SubModules.SCHEDULE,
            action = TimetableEndpoint.Actions.TIMETABLE,
            actionType = TimetableEndpoint.ActionTypes.VIEW,
            payload = filterPayload(filter)
        )
        return JsonRowUtils.asRows(data).map { it.toTimetableEntry() }
    }

    /** m_timetable contract: sm_configuration/workingDay/view. */
    override suspend fun getWorkingDays(): List<WorkingDay> {
        val data = apiClient.request<JsonElement?>(
            module = TimetableEndpoint.MODULE,
            submodule = TimetableEndpoint.SubModules.CONFIGURATION,
            action = TimetableEndpoint.Actions.WORKING_DAY,
            actionType = TimetableEndpoint.ActionTypes.VIEW,
            payload = instIdPayload()
        )
        return JsonRowUtils.asRows(data).map { it.toWorkingDay() }
    }

    /** m_timetable contract: sm_configuration/timeSlot/view. */
    override suspend fun getTimeSlots(): List<TimeSlot> {
        val data = apiClient.request<JsonElement?>(
            module = TimetableEndpoint.MODULE,
            submodule = TimetableEndpoint.SubModules.CONFIGURATION,
            action = TimetableEndpoint.Actions.TIME_SLOT,
            actionType = TimetableEndpoint.ActionTypes.VIEW,
            payload = instIdPayload()
        )
        return JsonRowUtils.asRows(data).map { it.toTimeSlot() }
    }

    /** m_timetable contract: sm_configuration/academicCalendar/view - a single
     * proxy-metadata object, not a row list (see AcademicCalendar's doc comment). */
    override suspend fun getAcademicCalendar(): AcademicCalendar? {
        val data = apiClient.request<JsonElement?>(
            module = TimetableEndpoint.MODULE,
            submodule = TimetableEndpoint.SubModules.CONFIGURATION,
            action = TimetableEndpoint.Actions.ACADEMIC_CALENDAR,
            actionType = TimetableEndpoint.ActionTypes.VIEW,
            payload = instIdPayload()
        )
        return JsonRowUtils.asRows(data).firstOrNull()?.toAcademicCalendar()
    }

    /** m_timetable contract: sm_allocation/facultyAllocation/view. */
    override suspend fun getFacultyAllocations(): List<FacultyAllocation> {
        val data = apiClient.request<JsonElement?>(
            module = TimetableEndpoint.MODULE,
            submodule = TimetableEndpoint.SubModules.ALLOCATION,
            action = TimetableEndpoint.Actions.FACULTY_ALLOCATION,
            actionType = TimetableEndpoint.ActionTypes.VIEW,
            payload = instIdPayload()
        )
        return JsonRowUtils.asRows(data).map { it.toFacultyAllocation() }
    }

    /** m_timetable contract: sm_allocation/roomAllocation/view. */
    override suspend fun getRoomAllocations(): List<RoomAllocation> {
        val data = apiClient.request<JsonElement?>(
            module = TimetableEndpoint.MODULE,
            submodule = TimetableEndpoint.SubModules.ALLOCATION,
            action = TimetableEndpoint.Actions.ROOM_ALLOCATION,
            actionType = TimetableEndpoint.ActionTypes.VIEW,
            payload = instIdPayload()
        )
        return JsonRowUtils.asRows(data).map { it.toRoomAllocation() }
    }

    /** m_timetable contract: sm_substitution/substitution/view. */
    override suspend fun getSubstitutions(): List<Substitution> {
        val data = apiClient.request<JsonElement?>(
            module = TimetableEndpoint.MODULE,
            submodule = TimetableEndpoint.SubModules.SUBSTITUTION,
            action = TimetableEndpoint.Actions.SUBSTITUTION,
            actionType = TimetableEndpoint.ActionTypes.VIEW,
            payload = instIdPayload()
        )
        return JsonRowUtils.asRows(data).map { it.toSubstitution() }
    }

    /** m_timetable contract: sm_approval/timetableApproval/view. */
    override suspend fun getTimetableApprovals(): List<TimetableApproval> {
        val data = apiClient.request<JsonElement?>(
            module = TimetableEndpoint.MODULE,
            submodule = TimetableEndpoint.SubModules.APPROVAL,
            action = TimetableEndpoint.Actions.TIMETABLE_APPROVAL,
            actionType = TimetableEndpoint.ActionTypes.VIEW,
            payload = instIdPayload()
        )
        return JsonRowUtils.asRows(data).map { it.toTimetableApproval() }
    }

    /** m_timetable contract: sm_schedule/timetable/view with `exportFormat` set
     * (contract v2 §4.1). */
    override suspend fun exportTimetable(format: String, filter: TimetableFilter): TimetableExportResult {
        val payload = filterPayload(filter)
        payload["exportFormat"] = format
        val data = apiClient.request<JsonElement?>(
            module = TimetableEndpoint.MODULE,
            submodule = TimetableEndpoint.SubModules.SCHEDULE,
            action = TimetableEndpoint.Actions.TIMETABLE,
            actionType = TimetableEndpoint.ActionTypes.VIEW,
            payload = payload
        )
        return JsonRowUtils.asObject(data)?.toTimetableExportResult()
            ?: throw APIError.BusinessError("", "The server did not confirm the export. Please try again.")
    }

    /** m_timetable contract: sm_approval/timetableApproval/add (contract v2 §7.1). */
    override suspend fun submitTimetableApproval(timetableId: String, remarks: String?): TimetableApproval {
        val payload = instIdPayload()
        payload["timetableId"] = timetableId.toLongOrNull() ?: timetableId
        remarks?.takeIf { it.isNotBlank() }?.let { payload["remarks"] = it }

        val data = apiClient.request<JsonElement?>(
            module = TimetableEndpoint.MODULE,
            submodule = TimetableEndpoint.SubModules.APPROVAL,
            action = TimetableEndpoint.Actions.TIMETABLE_APPROVAL,
            actionType = TimetableEndpoint.ActionTypes.ADD,
            payload = payload
        )
        return JsonRowUtils.asObject(data)?.toTimetableApproval()
            ?: TimetableApproval(
                id = null, institutionId = null, academicYearId = null, termId = null,
                sectionId = null, ttCode = null, statusId = "PENDING_APPROVAL", description = remarks
            )
    }

    /** m_timetable contract: sm_approval/timetableApproval/update - `action` is
     * `"APPROVE"` or `"REJECT"` (contract v2 §7.1). */
    override suspend fun decideTimetableApproval(
        timetableId: String,
        approve: Boolean,
        remarks: String?
    ): TimetableApproval {
        val payload = instIdPayload()
        payload["timetableId"] = timetableId.toLongOrNull() ?: timetableId
        payload["action"] = if (approve) "APPROVE" else "REJECT"
        remarks?.takeIf { it.isNotBlank() }?.let { payload["remarks"] = it }

        val data = apiClient.request<JsonElement?>(
            module = TimetableEndpoint.MODULE,
            submodule = TimetableEndpoint.SubModules.APPROVAL,
            action = TimetableEndpoint.Actions.TIMETABLE_APPROVAL,
            actionType = TimetableEndpoint.ActionTypes.UPDATE,
            payload = payload
        )
        return JsonRowUtils.asObject(data)?.toTimetableApproval()
            ?: TimetableApproval(
                id = null, institutionId = null, academicYearId = null, termId = null,
                sectionId = null, ttCode = null,
                statusId = if (approve) "PUBLISHED" else "DRAFT", description = remarks
            )
    }

    /** m_timetable contract: sm_substitution/substitution/add (contract v2 §6.1). */
    override suspend fun requestSubstitution(
        timetableEntryId: String,
        substituteFacultyId: String,
        reason: String,
        originalFacultyId: String?,
        substitutionDate: String?,
        remarks: String?
    ): Substitution {
        val payload = instIdPayload()
        payload["timetableEntryId"] = timetableEntryId.toLongOrNull() ?: timetableEntryId
        payload["substituteFacultyId"] = substituteFacultyId.toLongOrNull() ?: substituteFacultyId
        payload["reason"] = reason
        originalFacultyId?.let { payload["originalFacultyId"] = it.toLongOrNull() ?: it }
        substitutionDate?.takeIf { it.isNotBlank() }?.let { payload["substitutionDate"] = it }
        remarks?.takeIf { it.isNotBlank() }?.let { payload["remarks"] = it }

        val data = apiClient.request<JsonElement?>(
            module = TimetableEndpoint.MODULE,
            submodule = TimetableEndpoint.SubModules.SUBSTITUTION,
            action = TimetableEndpoint.Actions.SUBSTITUTION,
            actionType = TimetableEndpoint.ActionTypes.ADD,
            payload = payload
        )
        return JsonRowUtils.asObject(data)?.toSubstitution()
            ?: Substitution(
                id = null, classSessionId = timetableEntryId, ttEntryId = timetableEntryId,
                changeTypeId = "FAC_CHANGE", oldFacultyId = originalFacultyId,
                newFacultyId = substituteFacultyId, oldRoomId = null, newRoomId = null,
                oldSessionDate = null, newSessionDate = substitutionDate, reason = reason,
                status = "ACTIVE", remarks = remarks
            )
    }

    /** m_timetable contract: sm_substitution/substitution/delete (contract v2 §6.1
     * "Revoke Substitution"). */
    override suspend fun cancelSubstitution(id: String, reason: String) {
        val payload = instIdPayload()
        payload["id"] = id.toLongOrNull() ?: id
        payload["reason"] = reason

        apiClient.request<JsonElement?>(
            module = TimetableEndpoint.MODULE,
            submodule = TimetableEndpoint.SubModules.SUBSTITUTION,
            action = TimetableEndpoint.Actions.SUBSTITUTION,
            actionType = TimetableEndpoint.ActionTypes.DELETE,
            payload = payload
        )
    }
}
