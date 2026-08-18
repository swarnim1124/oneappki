package com.xsc.oneapp.feature.timetable.domain.usecase

import com.xsc.oneapp.feature.timetable.domain.model.Substitution
import com.xsc.oneapp.feature.timetable.domain.repository.TimetableRepository
import javax.inject.Inject

/** `sm_substitution/substitution/add` (contract v2 §6.1). Only one active
 * substitution can exist per class session at a time - a second attempt against
 * the same entry surfaces as a business error from the backend. */
class RequestSubstitutionUseCase @Inject constructor(
    private val repository: TimetableRepository
) {
    suspend operator fun invoke(
        timetableEntryId: String,
        substituteFacultyId: String,
        reason: String,
        originalFacultyId: String? = null,
        substitutionDate: String? = null,
        remarks: String? = null
    ): Substitution = repository.requestSubstitution(
        timetableEntryId, substituteFacultyId, reason, originalFacultyId, substitutionDate, remarks
    )
}
