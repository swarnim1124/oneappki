package com.xsc.oneapp.feature.timetable.domain.usecase

import com.xsc.oneapp.feature.timetable.domain.model.TimetableExportResult
import com.xsc.oneapp.feature.timetable.domain.model.TimetableFilter
import com.xsc.oneapp.feature.timetable.domain.repository.TimetableRepository
import javax.inject.Inject

/** `sm_schedule/timetable/view` with `exportFormat` set (contract v2 §4.1: pdf,
 * excel, csv). See [TimetableExportResult] for what the response actually carries. */
class ExportTimetableUseCase @Inject constructor(
    private val repository: TimetableRepository
) {
    suspend operator fun invoke(
        format: String,
        filter: TimetableFilter = TimetableFilter()
    ): TimetableExportResult = repository.exportTimetable(format, filter)
}
