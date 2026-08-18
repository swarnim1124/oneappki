package com.xsc.oneapp.feature.timetable.domain.usecase

import com.xsc.oneapp.feature.timetable.domain.model.TimetableApproval
import com.xsc.oneapp.feature.timetable.domain.repository.TimetableRepository
import javax.inject.Inject

/** `sm_approval/timetableApproval/update` - PENDING_APPROVAL -> PUBLISHED (approve)
 * or -> DRAFT (reject), contract v2 §7.1. */
class DecideTimetableApprovalUseCase @Inject constructor(
    private val repository: TimetableRepository
) {
    suspend operator fun invoke(
        timetableId: String,
        approve: Boolean,
        remarks: String? = null
    ): TimetableApproval = repository.decideTimetableApproval(timetableId, approve, remarks)
}
