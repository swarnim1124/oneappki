package com.xsc.oneapp.feature.timetable.domain.usecase

import com.xsc.oneapp.feature.timetable.domain.repository.TimetableRepository
import javax.inject.Inject

/** `sm_substitution/substitution/delete` (contract v2 §6.1). */
class CancelSubstitutionUseCase @Inject constructor(
    private val repository: TimetableRepository
) {
    suspend operator fun invoke(id: String, reason: String) = repository.cancelSubstitution(id, reason)
}
