package com.xsc.oneapp.feature.timetable.data.repository

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.xsc.oneapp.feature.timetable.data.network.TimetableEndpoint
import com.xsc.oneapp.feature.timetable.domain.model.TimetableFilter
import com.xsc.sdk.auth.SessionManager
import com.xsc.sdk.network.APIClient
import com.xsc.sdk.network.APIError
import com.xsc.sdk.network.api.DispatchRequest
import com.xsc.sdk.network.api.DispatchResponse
import com.xsc.sdk.network.internal.DispatcherApi
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.Response

/**
 * m_timetable rows are raw SQLAlchemy-model dictionaries (same pattern as
 * m_attendance/m_curriculum/m_fees) - see TimetableMapper.kt. Field names below are
 * the real confirmed response shapes (2026-07-31), not guessed.
 *
 * `getInstitutionId()` defaults to `1` here (not `null`): confirmed 2026-08-16 that
 * `inst_id` is a hard requirement of this module's gateway, so every "happy path"
 * test below needs a session that has one. The no-institution-id case is its own
 * test now (`throws a clear error when the session doesn't have one`) rather than
 * the previous default.
 */
class TimetableRepositoryImplTest {

    private val gson = Gson()

    private fun repository(
        dispatcherApi: DispatcherApi,
        sessionManager: SessionManager = mockk<SessionManager>().also {
            every { it.getInstitutionId() } returns 1
        }
    ) = TimetableRepositoryImpl(APIClient(dispatcherApi, gson), sessionManager)

    @Test
    fun `getTimetableEntries dispatches to sm_schedule timetable view and maps rows`() = runTest {
        val dispatcherApi = mockk<DispatcherApi>()
        val requestSlot = slot<DispatchRequest>()
        val rows = JsonParser.parseString(
            """[{"id":1,"tt_id":5,"inst_id":1,"academic_year_id":2026,"term_id":1,"program_id":10,"semester_id":2,"sec_id":3,"course_id":"CS101","crs_offering_id":1,"fac_id":201,"fac_crs_assignment_id":201,"working_day_id":1,"day_of_week":"MONDAY","time_slot_id":2,"room_id":50,"session_type_id":"LECTURE","start_date":"2026-01-01","end_date":"2026-06-30","is_active":true,"tt_code":"TT_SEC3_TERM1","tt_status":"PUBLISHED"}]"""
        )
        coEvery { dispatcherApi.dispatch(capture(requestSlot)) } returns
            Response.success(DispatchResponse(status = "success", data = rows))

        val result = repository(dispatcherApi).getTimetableEntries()

        assertEquals(1, result.size)
        val entry = result.first()
        assertEquals("5", entry.ttId)
        assertEquals("CS101", entry.courseId)
        assertEquals("201", entry.facultyId)
        assertEquals("50", entry.roomId)
        assertEquals("MONDAY", entry.dayOfWeek)
        assertEquals("TT_SEC3_TERM1", entry.ttCode)
        assertEquals("PUBLISHED", entry.ttStatus)
        assertEquals(TimetableEndpoint.MODULE, requestSlot.captured.mod)
        assertEquals(TimetableEndpoint.SubModules.SCHEDULE, requestSlot.captured.subMod)
        assertEquals(TimetableEndpoint.Actions.TIMETABLE, requestSlot.captured.action)
        assertEquals(TimetableEndpoint.ActionTypes.VIEW, requestSlot.captured.actionType)
    }

    @Test
    fun `includes inst_id when the session has one`() = runTest {
        val dispatcherApi = mockk<DispatcherApi>()
        val requestSlot = slot<DispatchRequest>()
        coEvery { dispatcherApi.dispatch(capture(requestSlot)) } returns
            Response.success(DispatchResponse(status = "success", data = JsonParser.parseString("[]")))
        val sessionManager = mockk<SessionManager>()
        every { sessionManager.getInstitutionId() } returns 1

        repository(dispatcherApi, sessionManager).getTimetableEntries()

        assertEquals(1, requestSlot.captured.payload["inst_id"])
    }

    @Test
    fun `throws a clear error when the session doesn't have one`() = runTest {
        val dispatcherApi = mockk<DispatcherApi>()
        val sessionManager = mockk<SessionManager>()
        every { sessionManager.getInstitutionId() } returns null

        try {
            repository(dispatcherApi, sessionManager).getTimetableEntries()
            fail("Expected an APIError.BusinessError")
        } catch (e: APIError.BusinessError) {
            assertTrue(e.errorMessage.contains("log", ignoreCase = true))
        }
    }

    @Test
    fun `getWorkingDays dispatches to sm_configuration workingDay view and maps rows`() = runTest {
        val dispatcherApi = mockk<DispatcherApi>()
        val requestSlot = slot<DispatchRequest>()
        val rows = JsonParser.parseString(
            """[{"id":1,"inst_id":1,"academic_year_id":2026,"name":"Standard Week","effective_from":"2026-01-01","effective_to":"2026-12-31","day_of_week_id":1,"day_name":"MONDAY","is_working_day":true,"is_active":true}]"""
        )
        coEvery { dispatcherApi.dispatch(capture(requestSlot)) } returns
            Response.success(DispatchResponse(status = "success", data = rows))

        val result = repository(dispatcherApi).getWorkingDays()

        assertEquals(1, result.size)
        assertEquals("Standard Week", result.first().name)
        assertEquals("MONDAY", result.first().dayName)
        assertEquals(TimetableEndpoint.SubModules.CONFIGURATION, requestSlot.captured.subMod)
        assertEquals(TimetableEndpoint.Actions.WORKING_DAY, requestSlot.captured.action)
    }

    @Test
    fun `getTimeSlots dispatches to sm_configuration timeSlot view and maps rows`() = runTest {
        val dispatcherApi = mockk<DispatcherApi>()
        val requestSlot = slot<DispatchRequest>()
        val rows = JsonParser.parseString(
            """[{"id":1,"inst_id":1,"slot_name":"Period 1","start_time":"09:00:00","end_time":"09:50:00","slot_sequence":1,"is_break":false,"is_active":true}]"""
        )
        coEvery { dispatcherApi.dispatch(capture(requestSlot)) } returns
            Response.success(DispatchResponse(status = "success", data = rows))

        val result = repository(dispatcherApi).getTimeSlots()

        assertEquals(1, result.size)
        assertEquals("Period 1", result.first().slotName)
        assertEquals("09:00:00", result.first().startTime)
        assertEquals(TimetableEndpoint.SubModules.CONFIGURATION, requestSlot.captured.subMod)
        assertEquals(TimetableEndpoint.Actions.TIME_SLOT, requestSlot.captured.action)
    }

    @Test
    fun `getAcademicCalendar dispatches to sm_configuration academicCalendar view and maps the single proxy object`() = runTest {
        val dispatcherApi = mockk<DispatcherApi>()
        val requestSlot = slot<DispatchRequest>()
        val obj = JsonParser.parseString(
            """{"inst_id":1,"academicYearId":2026,"termId":1,"note":"Academic calendar data is managed by the academic structure module. This view proxies the relevant term data for timetable usage."}"""
        )
        coEvery { dispatcherApi.dispatch(capture(requestSlot)) } returns
            Response.success(DispatchResponse(status = "success", data = obj))

        val result = repository(dispatcherApi).getAcademicCalendar()

        assertNotNull(result)
        assertEquals("2026", result!!.academicYearId)
        assertEquals("1", result.termId)
        assertEquals(TimetableEndpoint.SubModules.CONFIGURATION, requestSlot.captured.subMod)
        assertEquals(TimetableEndpoint.Actions.ACADEMIC_CALENDAR, requestSlot.captured.action)
    }

    @Test
    fun `getAcademicCalendar returns null when the response has no data`() = runTest {
        val dispatcherApi = mockk<DispatcherApi>()
        coEvery { dispatcherApi.dispatch(any()) } returns
            Response.success(DispatchResponse(status = "success", data = null))

        val result = repository(dispatcherApi).getAcademicCalendar()

        assertNull(result)
    }

    @Test
    fun `getFacultyAllocations dispatches to sm_allocation facultyAllocation view and maps rows`() = runTest {
        val dispatcherApi = mockk<DispatcherApi>()
        val requestSlot = slot<DispatchRequest>()
        val rows = JsonParser.parseString(
            """[{"id":1,"crs_offering_id":12,"fac_id":201,"assignment_role_id":"PRIMARY","workload_percent":100,"is_primary":true,"remarks":"Assigned HOD approved"}]"""
        )
        coEvery { dispatcherApi.dispatch(capture(requestSlot)) } returns
            Response.success(DispatchResponse(status = "success", data = rows))

        val result = repository(dispatcherApi).getFacultyAllocations()

        assertEquals(1, result.size)
        assertEquals("PRIMARY", result.first().assignmentRoleId)
        assertEquals("Assigned HOD approved", result.first().remarks)
        assertEquals(TimetableEndpoint.SubModules.ALLOCATION, requestSlot.captured.subMod)
        assertEquals(TimetableEndpoint.Actions.FACULTY_ALLOCATION, requestSlot.captured.action)
    }

    @Test
    fun `getRoomAllocations dispatches to sm_allocation roomAllocation view and maps rows`() = runTest {
        val dispatcherApi = mockk<DispatcherApi>()
        val requestSlot = slot<DispatchRequest>()
        val rows = JsonParser.parseString(
            """[{"id":1,"tt_id":5,"room_id":50,"day_of_week":"MONDAY","time_slot_id":2}]"""
        )
        coEvery { dispatcherApi.dispatch(capture(requestSlot)) } returns
            Response.success(DispatchResponse(status = "success", data = rows))

        val result = repository(dispatcherApi).getRoomAllocations()

        assertEquals(1, result.size)
        assertEquals("50", result.first().roomId)
        assertEquals(TimetableEndpoint.SubModules.ALLOCATION, requestSlot.captured.subMod)
        assertEquals(TimetableEndpoint.Actions.ROOM_ALLOCATION, requestSlot.captured.action)
    }

    @Test
    fun `getSubstitutions dispatches to sm_substitution substitution view and maps rows`() = runTest {
        val dispatcherApi = mockk<DispatcherApi>()
        val requestSlot = slot<DispatchRequest>()
        val rows = JsonParser.parseString(
            """[{"id":1,"class_session_id":501,"tt_entry_id":1,"change_type_id":"FAC_CHANGE","old_fac_id":201,"new_fac_id":205,"old_room_id":50,"new_room_id":50,"old_session_date":"2026-02-15","new_session_date":"2026-02-15","reason":"Faculty on sick leave","status":"ACTIVE"}]"""
        )
        coEvery { dispatcherApi.dispatch(capture(requestSlot)) } returns
            Response.success(DispatchResponse(status = "success", data = rows))

        val result = repository(dispatcherApi).getSubstitutions()

        assertEquals(1, result.size)
        val sub = result.first()
        assertEquals("201", sub.oldFacultyId)
        assertEquals("205", sub.newFacultyId)
        assertEquals("Faculty on sick leave", sub.reason)
        assertEquals("ACTIVE", sub.status)
        assertEquals(TimetableEndpoint.SubModules.SUBSTITUTION, requestSlot.captured.subMod)
        assertEquals(TimetableEndpoint.Actions.SUBSTITUTION, requestSlot.captured.action)
    }

    @Test
    fun `getTimetableApprovals dispatches to sm_approval timetableApproval view and maps rows`() = runTest {
        val dispatcherApi = mockk<DispatcherApi>()
        val requestSlot = slot<DispatchRequest>()
        val rows = JsonParser.parseString(
            """[{"id":5,"inst_id":1,"academic_year_id":2026,"term_id":1,"sec_id":3,"tt_code":"TT_SEC3_TERM1","status_id":"PENDING_APPROVAL","description":"Timetable draft submitted for Dean review"}]"""
        )
        coEvery { dispatcherApi.dispatch(capture(requestSlot)) } returns
            Response.success(DispatchResponse(status = "success", data = rows))

        val result = repository(dispatcherApi).getTimetableApprovals()

        assertEquals(1, result.size)
        val approval = result.first()
        assertEquals("PENDING_APPROVAL", approval.statusId)
        assertEquals("Timetable draft submitted for Dean review", approval.description)
        assertEquals(TimetableEndpoint.SubModules.APPROVAL, requestSlot.captured.subMod)
        assertEquals(TimetableEndpoint.Actions.TIMETABLE_APPROVAL, requestSlot.captured.action)
    }

    @Test
    fun `getTimetableEntries sends the filter's non-null fields under contract camelCase keys`() = runTest {
        val dispatcherApi = mockk<DispatcherApi>()
        val requestSlot = slot<DispatchRequest>()
        coEvery { dispatcherApi.dispatch(capture(requestSlot)) } returns
            Response.success(DispatchResponse(status = "success", data = JsonParser.parseString("[]")))

        repository(dispatcherApi).getTimetableEntries(
            TimetableFilter(sectionId = "3", facultyId = "201", termId = "1", academicYearId = "2026")
        )

        val payload = requestSlot.captured.payload
        assertEquals("3", payload["sectionId"])
        assertEquals("201", payload["facultyId"])
        assertEquals("1", payload["termId"])
        assertEquals("2026", payload["academicYearId"])
        assertEquals(1, payload["inst_id"])
        assertTrue(!payload.containsKey("roomId"))
    }

    @Test
    fun `exportTimetable sends exportFormat alongside the filter and maps the export metadata`() = runTest {
        val dispatcherApi = mockk<DispatcherApi>()
        val requestSlot = slot<DispatchRequest>()
        val obj = JsonParser.parseString(
            """{"exportFormat":"pdf","mimeType":"application/pdf","filename":"timetable_sec3.pdf","recordCount":24}"""
        )
        coEvery { dispatcherApi.dispatch(capture(requestSlot)) } returns
            Response.success(DispatchResponse(status = "success", data = obj))

        val result = repository(dispatcherApi).exportTimetable("pdf", TimetableFilter(sectionId = "3"))

        assertEquals("timetable_sec3.pdf", result.filename)
        assertEquals(24, result.recordCount)
        assertEquals("pdf", requestSlot.captured.payload["exportFormat"])
        assertEquals("3", requestSlot.captured.payload["sectionId"])
        assertEquals(TimetableEndpoint.SubModules.SCHEDULE, requestSlot.captured.subMod)
        assertEquals(TimetableEndpoint.ActionTypes.VIEW, requestSlot.captured.actionType)
    }

    @Test
    fun `exportTimetable raises a clear error when the server doesn't confirm the export`() = runTest {
        val dispatcherApi = mockk<DispatcherApi>()
        coEvery { dispatcherApi.dispatch(any()) } returns
            Response.success(DispatchResponse(status = "success", data = null))

        try {
            repository(dispatcherApi).exportTimetable("csv")
            fail("Expected an APIError.BusinessError")
        } catch (e: APIError.BusinessError) {
            assertTrue(e.errorMessage.contains("export", ignoreCase = true))
        }
    }

    @Test
    fun `submitTimetableApproval dispatches to sm_approval timetableApproval add`() = runTest {
        val dispatcherApi = mockk<DispatcherApi>()
        val requestSlot = slot<DispatchRequest>()
        val obj = JsonParser.parseString(
            """{"id":5,"status_id":"PENDING_APPROVAL","tt_code":"TT_SEC3_TERM1"}"""
        )
        coEvery { dispatcherApi.dispatch(capture(requestSlot)) } returns
            Response.success(DispatchResponse(status = "success", data = obj))

        val result = repository(dispatcherApi).submitTimetableApproval("5", "ready for review")

        assertEquals("PENDING_APPROVAL", result.statusId)
        assertEquals(5L, requestSlot.captured.payload["timetableId"])
        assertEquals("ready for review", requestSlot.captured.payload["remarks"])
        assertEquals(TimetableEndpoint.SubModules.APPROVAL, requestSlot.captured.subMod)
        assertEquals(TimetableEndpoint.ActionTypes.ADD, requestSlot.captured.actionType)
    }

    @Test
    fun `submitTimetableApproval falls back to a synthesized PENDING_APPROVAL result when the server confirms nothing`() = runTest {
        val dispatcherApi = mockk<DispatcherApi>()
        coEvery { dispatcherApi.dispatch(any()) } returns
            Response.success(DispatchResponse(status = "success", data = null))

        val result = repository(dispatcherApi).submitTimetableApproval("5", null)

        assertEquals("PENDING_APPROVAL", result.statusId)
    }

    @Test
    fun `decideTimetableApproval sends APPROVE or REJECT as the action`() = runTest {
        val dispatcherApi = mockk<DispatcherApi>()
        val approveSlot = slot<DispatchRequest>()
        coEvery { dispatcherApi.dispatch(capture(approveSlot)) } returns
            Response.success(DispatchResponse(status = "success", data = JsonParser.parseString("""{"status_id":"PUBLISHED"}""")))

        val approved = repository(dispatcherApi).decideTimetableApproval("5", approve = true, remarks = null)

        assertEquals("APPROVE", approveSlot.captured.payload["action"])
        assertEquals("PUBLISHED", approved.statusId)
        assertEquals(TimetableEndpoint.ActionTypes.UPDATE, approveSlot.captured.actionType)

        val rejectSlot = slot<DispatchRequest>()
        coEvery { dispatcherApi.dispatch(capture(rejectSlot)) } returns
            Response.success(DispatchResponse(status = "success", data = JsonParser.parseString("""{"status_id":"DRAFT"}""")))

        val rejected = repository(dispatcherApi).decideTimetableApproval("5", approve = false, remarks = "needs fixes")

        assertEquals("REJECT", rejectSlot.captured.payload["action"])
        assertEquals("needs fixes", rejectSlot.captured.payload["remarks"])
        assertEquals("DRAFT", rejected.statusId)
    }

    @Test
    fun `requestSubstitution dispatches to sm_substitution substitution add with the reason required and remarks optional`() = runTest {
        val dispatcherApi = mockk<DispatcherApi>()
        val requestSlot = slot<DispatchRequest>()
        val obj = JsonParser.parseString(
            """{"id":2,"old_fac_id":201,"new_fac_id":205,"status":"ACTIVE"}"""
        )
        coEvery { dispatcherApi.dispatch(capture(requestSlot)) } returns
            Response.success(DispatchResponse(status = "success", data = obj))

        val result = repository(dispatcherApi).requestSubstitution(
            timetableEntryId = "1",
            substituteFacultyId = "205",
            reason = "sick leave",
            originalFacultyId = "201",
            substitutionDate = "2026-02-15",
            remarks = null
        )

        assertEquals("201", result.oldFacultyId)
        assertEquals("205", result.newFacultyId)
        assertEquals("ACTIVE", result.status)
        assertEquals(1L, requestSlot.captured.payload["timetableEntryId"])
        assertEquals(205L, requestSlot.captured.payload["substituteFacultyId"])
        assertEquals("sick leave", requestSlot.captured.payload["reason"])
        assertEquals("2026-02-15", requestSlot.captured.payload["substitutionDate"])
        assertTrue(!requestSlot.captured.payload.containsKey("remarks"))
        assertEquals(TimetableEndpoint.SubModules.SUBSTITUTION, requestSlot.captured.subMod)
        assertEquals(TimetableEndpoint.ActionTypes.ADD, requestSlot.captured.actionType)
    }

    @Test
    fun `cancelSubstitution dispatches to sm_substitution substitution delete with the id and reason`() = runTest {
        val dispatcherApi = mockk<DispatcherApi>()
        val requestSlot = slot<DispatchRequest>()
        coEvery { dispatcherApi.dispatch(capture(requestSlot)) } returns
            Response.success(DispatchResponse(status = "success", data = null))

        repository(dispatcherApi).cancelSubstitution("2", "no longer needed")

        assertEquals(2L, requestSlot.captured.payload["id"])
        assertEquals("no longer needed", requestSlot.captured.payload["reason"])
        assertEquals(TimetableEndpoint.SubModules.SUBSTITUTION, requestSlot.captured.subMod)
        assertEquals(TimetableEndpoint.ActionTypes.DELETE, requestSlot.captured.actionType)
    }
}
