package com.xsc.oneapp.feature.timetable.domain.usecase

import com.xsc.oneapp.feature.timetable.domain.model.Substitution
import com.xsc.oneapp.feature.timetable.domain.repository.TimetableRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RequestSubstitutionUseCaseTest {

    @Test
    fun `invoke delegates all fields to the repository`() = runTest {
        val repository = mockk<TimetableRepository>()
        val substitution = Substitution(
            "2", "501", "1", "FAC_CHANGE", "201", "205", "50", "50",
            "2026-02-15", "2026-02-15", "sick leave", "ACTIVE"
        )
        coEvery {
            repository.requestSubstitution("1", "205", "sick leave", "201", "2026-02-15", "cover arranged")
        } returns substitution

        val result = RequestSubstitutionUseCase(repository).invoke(
            timetableEntryId = "1",
            substituteFacultyId = "205",
            reason = "sick leave",
            originalFacultyId = "201",
            substitutionDate = "2026-02-15",
            remarks = "cover arranged"
        )

        assertEquals(substitution, result)
    }

    @Test
    fun `invoke defaults the optional fields to null`() = runTest {
        val repository = mockk<TimetableRepository>()
        val substitution = Substitution(
            "2", "501", "1", "FAC_CHANGE", null, "205", null, null,
            null, null, "sick leave", "ACTIVE"
        )
        coEvery {
            repository.requestSubstitution("1", "205", "sick leave", null, null, null)
        } returns substitution

        val result = RequestSubstitutionUseCase(repository).invoke(
            timetableEntryId = "1",
            substituteFacultyId = "205",
            reason = "sick leave"
        )

        assertEquals(substitution, result)
    }
}
