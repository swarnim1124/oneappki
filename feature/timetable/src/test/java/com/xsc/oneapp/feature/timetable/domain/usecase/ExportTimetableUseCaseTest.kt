package com.xsc.oneapp.feature.timetable.domain.usecase

import com.xsc.oneapp.feature.timetable.domain.model.TimetableExportResult
import com.xsc.oneapp.feature.timetable.domain.model.TimetableFilter
import com.xsc.oneapp.feature.timetable.domain.repository.TimetableRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ExportTimetableUseCaseTest {

    @Test
    fun `invoke delegates the format and filter to the repository`() = runTest {
        val repository = mockk<TimetableRepository>()
        val filter = TimetableFilter(sectionId = "3")
        val result = TimetableExportResult("pdf", "application/pdf", "timetable_sec3.pdf", 24)
        coEvery { repository.exportTimetable("pdf", filter) } returns result

        val actual = ExportTimetableUseCase(repository).invoke("pdf", filter)

        assertEquals(result, actual)
    }

    @Test
    fun `invoke defaults to no filter when none is supplied`() = runTest {
        val repository = mockk<TimetableRepository>()
        val result = TimetableExportResult("csv", "text/csv", "timetable.csv", 0)
        coEvery { repository.exportTimetable("csv", TimetableFilter()) } returns result

        val actual = ExportTimetableUseCase(repository).invoke("csv")

        assertEquals(result, actual)
    }
}
