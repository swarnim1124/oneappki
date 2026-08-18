package com.xsc.oneapp.feature.timetable.domain.usecase

import com.xsc.oneapp.feature.timetable.domain.model.TimetableApproval
import com.xsc.oneapp.feature.timetable.domain.repository.TimetableRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DecideTimetableApprovalUseCaseTest {

    @Test
    fun `invoke delegates approve to the repository`() = runTest {
        val repository = mockk<TimetableRepository>()
        val approval = TimetableApproval("5", "1", "2026", "1", "3", "TT_SEC3_TERM1", "PUBLISHED", null)
        coEvery { repository.decideTimetableApproval("5", true, null) } returns approval

        val result = DecideTimetableApprovalUseCase(repository).invoke("5", approve = true)

        assertEquals(approval, result)
    }

    @Test
    fun `invoke delegates reject with remarks to the repository`() = runTest {
        val repository = mockk<TimetableRepository>()
        val approval = TimetableApproval("5", "1", "2026", "1", "3", "TT_SEC3_TERM1", "DRAFT", "needs fixes")
        coEvery { repository.decideTimetableApproval("5", false, "needs fixes") } returns approval

        val result = DecideTimetableApprovalUseCase(repository).invoke("5", approve = false, remarks = "needs fixes")

        assertEquals(approval, result)
    }
}
