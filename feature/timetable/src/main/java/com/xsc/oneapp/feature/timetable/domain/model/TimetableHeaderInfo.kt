package com.xsc.oneapp.feature.timetable.domain.model

/**
 * The header strip (spec §1: academic year, term, section, timetable code, version,
 * effective range, status) assembled client side from whichever loaded entry/
 * approval rows share the timetable currently in view - there is no dedicated
 * "timetable header" endpoint, only `tb_tt` columns joined onto entry rows (see
 * [TimetableEntry.ttCode] and friends) and `tb_tt` rows themselves from
 * `timetableApproval:view` (contract v2 §7.1).
 *
 * When [TimetableFilter.ttId] isn't set, the most common `ttId` among the loaded
 * entries is used - the common case is a screen already scoped to one section/
 * faculty/room, where every entry shares one timetable.
 */
data class TimetableHeaderInfo(
    val timetableId: String?,
    val ttCode: String?,
    val status: String?,
    val academicYearId: String?,
    val termId: String?,
    val sectionId: String?,
    val versionNo: String?,
    val effectiveFrom: String?,
    val effectiveTo: String?,
    val isActive: Boolean?
) {
    companion object {
        fun from(
            entries: List<TimetableEntry>,
            approvals: List<TimetableApproval>,
            filter: TimetableFilter
        ): TimetableHeaderInfo? {
            if (entries.isEmpty() && approvals.isEmpty()) return null

            val ttId = filter.ttId
                ?: entries.mapNotNull { it.ttId }
                    .groupingBy { it }
                    .eachCount()
                    .maxByOrNull { it.value }
                    ?.key

            val entry = entries.firstOrNull { it.ttId == ttId } ?: entries.firstOrNull()
            val approval = approvals.firstOrNull { it.id == ttId } ?: approvals.firstOrNull()

            return TimetableHeaderInfo(
                timetableId = ttId ?: entry?.ttId ?: approval?.id,
                ttCode = entry?.ttCode ?: approval?.ttCode,
                status = entry?.ttStatus ?: approval?.statusId,
                academicYearId = entry?.academicYearId ?: approval?.academicYearId,
                termId = entry?.termId ?: approval?.termId,
                sectionId = entry?.sectionId ?: approval?.sectionId,
                versionNo = entry?.ttVersionNo ?: approval?.version,
                effectiveFrom = entry?.ttEffectiveFrom ?: entry?.startDate,
                effectiveTo = entry?.ttEffectiveTo ?: entry?.endDate,
                isActive = entry?.isActive?.isTruthy()
            )
        }

        private fun String.isTruthy(): Boolean = equals("true", ignoreCase = true) || this == "1"
    }
}
