package com.xsc.oneapp.feature.timetable.domain.usecase

import com.xsc.oneapp.feature.timetable.domain.model.TimetableApproval
import com.xsc.oneapp.feature.timetable.domain.repository.TimetableRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SubmitTimetableApprovalUseCaseTest {

    @Test
    fun `invoke delegates the timetable id and remarks to the repository`() = runTest {
        val repository = mockk<TimetableRepository>()
        val approval = TimetableApproval("5", "1", "2026", "1", "3", "TT_SEC3_TERM1", "PENDING_APPROVAL", null)
        coEvery { repository.submitTimetableApproval("5", "ready for review") } returns approval

        val result = SubmitTimetableApprovalUseCase(repository).invoke("5", "ready for review")

        assertEquals(approval, result)
    }

    @Test
    fun `invoke defaults remarks to null`() = runTest {
        val repository = mockk<TimetableRepository>()
        val approval = TimetableApproval("5", "1", "2026", "1", "3", "TT_SEC3_TERM1", "PENDING_APPROVAL", null)
        coEvery { repository.submitTimetableApproval("5", null) } returns approval

        val result = SubmitTimetableApprovalUseCase(repository).invoke("5")

        assertEquals(approval, result)
    }
}
