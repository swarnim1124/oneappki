package com.xsc.oneapp.feature.timetable.domain.usecase

import com.xsc.oneapp.feature.timetable.domain.model.TimetableApproval
import com.xsc.oneapp.feature.timetable.domain.repository.TimetableRepository
import javax.inject.Inject

/** `sm_approval/timetableApproval/add` - DRAFT -> PENDING_APPROVAL (contract v2
 * §7.1). Backend requires the timetable to be in DRAFT with at least one entry. */
class SubmitTimetableApprovalUseCase @Inject constructor(
    private val repository: TimetableRepository
) {
    suspend operator fun invoke(timetableId: String, remarks: String? = null): TimetableApproval =
        repository.submitTimetableApproval(timetableId, remarks)
}
