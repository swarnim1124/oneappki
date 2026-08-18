package com.xsc.oneapp.core.permissions

import com.xsc.sdk.auth.SessionManager
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionManagerPermissionCheckerTest {

    @Test
    fun `hasPermission delegates to SessionManager`() {
        val sessionManager = mockk<SessionManager>()
        every { sessionManager.hasPermission("timetable.timetable.view") } returns true
        every { sessionManager.hasPermission("timetable.timetable.add") } returns false
        val checker = SessionManagerPermissionChecker(sessionManager)

        assertTrue(checker.hasPermission("timetable.timetable.view"))
        assertFalse(checker.hasPermission("timetable.timetable.add"))
    }

    @Test
    fun `hasAnyPermission delegates to SessionManager with the same varargs`() {
        val sessionManager = mockk<SessionManager>()
        every {
            sessionManager.hasAnyPermission("attendance.attendance.view", "fees.fees.view")
        } returns true
        every {
            sessionManager.hasAnyPermission("attendance.attendance.view", "exam.exam.view")
        } returns false
        val checker = SessionManagerPermissionChecker(sessionManager)

        assertTrue(checker.hasAnyPermission("attendance.attendance.view", "fees.fees.view"))
        assertFalse(checker.hasAnyPermission("attendance.attendance.view", "exam.exam.view"))
    }

    @Test
    fun `permissions flow reflects SessionManager currentPermissions`() {
        val sessionManager = mockk<SessionManager>()
        every { sessionManager.currentPermissions } returns MutableStateFlow(listOf("profile.profile.view"))
        val checker = SessionManagerPermissionChecker(sessionManager)

        assertEquals(listOf("profile.profile.view"), checker.permissions.value)
    }
}
