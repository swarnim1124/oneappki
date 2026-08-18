package com.xsc.oneapp.feature.timetable.domain.model

/** Not a row list - the backend returns a single proxy-metadata object ("Academic
 * calendar data is managed by the academic structure module. This view proxies
 * the relevant term data for timetable usage."), so this is fetched as a single
 * nullable object rather than a List, same convention as profile's MedicalDetail.
 *
 * [adjustments] carries any holiday/compensatory-day rows the proxy nests alongside
 * that metadata (`tb_holiday_adjustment` per contract v2 §3.3/§8) under a plausible
 * array key (see TimetableMapper.toAcademicCalendar) - defensive-optional the same
 * way TimetableRepositoryImpl's `inst_id` handling is: costs nothing when the proxy
 * doesn't nest one, and is what lets requirement §11 (calendar adjustment
 * indicators) render without a second, unconfirmed endpoint. */
data class AcademicCalendar(
    val institutionId: String?,
    val academicYearId: String?,
    val termId: String?,
    val note: String?,
    val adjustments: List<AcademicCalendarAdjustment> = emptyList()
)

/** One `tb_holiday_adjustment` row (contract v2 §3.3 `academicCalendar:add` payload
 * shape - holidayDate/adjustmentTypeId/sourceWorkingDayId/targetWorkingDayId/
 * targetDate/remarks). */
data class AcademicCalendarAdjustment(
    val id: String?,
    val acadTermId: String?,
    val holidayDate: String?,
    val adjustmentTypeId: String?,
    val sourceWorkingDayId: String?,
    val targetWorkingDayId: String?,
    val targetDate: String?,
    val remarks: String?
)
