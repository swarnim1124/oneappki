package com.xsc.oneapp.feature.timetable.domain.usecase

import com.xsc.oneapp.feature.timetable.domain.model.TimetableEntry
import com.xsc.oneapp.feature.timetable.domain.model.TimetableFilter
import com.xsc.oneapp.feature.timetable.domain.repository.TimetableRepository
import javax.inject.Inject

class GetTimetableEntriesUseCase @Inject constructor(
    private val repository: TimetableRepository
) {
    suspend operator fun invoke(filter: TimetableFilter = TimetableFilter()): List<TimetableEntry> =
        repository.getTimetableEntries(filter)
}
