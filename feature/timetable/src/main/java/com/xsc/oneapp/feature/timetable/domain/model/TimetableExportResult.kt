package com.xsc.oneapp.feature.timetable.domain.model

/**
 * `sm_schedule/timetable/view` with `exportFormat` set (contract v2 §4.1). The
 * documented success response carries export metadata and the same entry rows
 * again (`"entries": [...]`) - no file URL or base64 payload is documented for any
 * of the three formats, so there is nothing here for the client to save or open as
 * a file. This is intentionally just the metadata: the UI's job is to confirm the
 * export ran and report what the backend named/sized it, not to fabricate a
 * download the contract doesn't provide.
 */
data class TimetableExportResult(
    val exportFormat: String?,
    val mimeType: String?,
    val filename: String?,
    val recordCount: Int?
)
