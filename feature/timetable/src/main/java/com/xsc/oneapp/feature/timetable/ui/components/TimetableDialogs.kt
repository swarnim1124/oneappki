package com.xsc.oneapp.feature.timetable.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val EXPORT_FORMATS = listOf("pdf" to "PDF", "excel" to "Excel", "csv" to "CSV")

/** spec §2 - export must support the contract's pdf/excel/csv (v2 §4.1). */
@Composable
fun ExportDialog(
    inFlight: Boolean,
    onDismiss: () -> Unit,
    onExport: (format: String) -> Unit
) {
    var selected by remember { mutableStateOf(EXPORT_FORMATS.first().first) }

    AlertDialog(
        onDismissRequest = { if (!inFlight) onDismiss() },
        title = { Text("Export timetable") },
        text = {
            Column {
                Text(
                    "Exports the current filtered view.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    EXPORT_FORMATS.forEach { (value, label) ->
                        FilterChip(selected = selected == value, onClick = { selected = value }, label = { Text(label) })
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onExport(selected) }, enabled = !inFlight) {
                if (inFlight) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Export")
                }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !inFlight) { Text("Cancel") } }
    )
}

/** spec §9 - Draft -> "Submit for approval". */
@Composable
fun SubmitForApprovalDialog(
    inFlight: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (remarks: String?) -> Unit
) {
    var remarks by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!inFlight) onDismiss() },
        title = { Text("Submit for approval") },
        text = {
            Column {
                Text(
                    "This moves the timetable from Draft to Pending approval.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Remarks (optional)") },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(remarks.trim().ifBlank { null }) }, enabled = !inFlight) {
                if (inFlight) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Submit")
                }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !inFlight) { Text("Cancel") } }
    )
}

/** spec §9 - Pending approval -> Approve/Reject. [approve] fixes which action this
 * dialog confirms; a rejection's remarks become the rejection reason shown back on
 * the (now Draft) timetable. */
@Composable
fun ApprovalDecisionDialog(
    approve: Boolean,
    inFlight: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (remarks: String?) -> Unit
) {
    var remarks by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!inFlight) onDismiss() },
        title = { Text(if (approve) "Approve timetable" else "Reject timetable") },
        text = {
            Column {
                Text(
                    if (approve) {
                        "This publishes the timetable - it will no longer be directly editable."
                    } else {
                        "This returns the timetable to Draft so it can be edited and resubmitted."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text(if (approve) "Remarks (optional)" else "Reason for rejection") },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(remarks.trim().ifBlank { null }) },
                enabled = !inFlight && (approve || remarks.isNotBlank())
            ) {
                if (inFlight) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(if (approve) "Approve" else "Reject")
                }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !inFlight) { Text("Cancel") } }
    )
}

/** spec §8 - Substitution action from the details drawer. [originalFacultyId]
 * pre-fills from the entry being substituted; the form only needs the replacement. */
@Composable
fun SubstitutionRequestDialog(
    originalFacultyId: String?,
    inFlight: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (substituteFacultyId: String, reason: String, date: String?, remarks: String?) -> Unit
) {
    var substituteFacultyId by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }

    val canSubmit = substituteFacultyId.isNotBlank() && reason.isNotBlank()

    AlertDialog(
        onDismissRequest = { if (!inFlight) onDismiss() },
        title = { Text("Request substitution") },
        text = {
            Column {
                if (originalFacultyId != null) {
                    Text(
                        "Original faculty #$originalFacultyId",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedTextField(
                    value = substituteFacultyId,
                    onValueChange = { substituteFacultyId = it },
                    label = { Text("Substitute faculty ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD, optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                )
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason") },
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                )
                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Remarks (optional)") },
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(substituteFacultyId.trim(), reason.trim(), date.trim().ifBlank { null }, remarks.trim().ifBlank { null })
                },
                enabled = !inFlight && canSubmit
            ) {
                if (inFlight) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Submit")
                }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !inFlight) { Text("Cancel") } }
    )
}

/** spec §8 - cancel an active substitution (contract v2 §6.1 delete/cancel). */
@Composable
fun CancelSubstitutionDialog(
    inFlight: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (reason: String) -> Unit
) {
    var reason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!inFlight) onDismiss() },
        title = { Text("Cancel substitution") },
        text = {
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Reason") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(reason.trim()) }, enabled = !inFlight && reason.isNotBlank()) {
                if (inFlight) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Confirm")
                }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !inFlight) { Text("Keep it") } }
    )
}
