package cn.szu.blankxiao.mycalendar.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cn.szu.blankxiao.mycalendar.ui.component.dialog.AddEditScheduleFormContent
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

/**
 * PC 端添加日程对话框（复用 AddEditScheduleFormContent）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesktopAddScheduleDialog(
    selectedDate: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (title: String, date: LocalDate, description: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf(selectedDate) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加日程") },
        text = {
            AddEditScheduleFormContent(
                title = title,
                onTitleChange = { title = it },
                description = description,
                onDescriptionChange = { description = it },
                date = currentDate,
                onDateClick = { showDatePicker = true },
                reminderEnabled = false,
                onReminderToggle = { },
                reminderDateTime = null,
                onReminderClick = { },
                showReminder = false,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, currentDate, description)
                    }
                }
            ) {
                Text("添加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            currentDate = Instant.fromEpochMilliseconds(millis)
                                .toLocalDateTime(TimeZone.currentSystemDefault()).date
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("确定")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
