package com.xsc.oneapp.feature.timetable.domain.usecase

import com.xsc.oneapp.feature.timetable.domain.repository.TimetableRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CancelSubstitutionUseCaseTest {

    @Test
    fun `invoke delegates the id and reason to the repository`() = runTest {
        val repository = mockk<TimetableRepository>()
        coEvery { repository.cancelSubstitution("2", "no longer needed") } returns Unit

        CancelSubstitutionUseCase(repository).invoke("2", "no longer needed")

        coVerify(exactly = 1) { repository.cancelSubstitution("2", "no longer needed") }
    }
}
