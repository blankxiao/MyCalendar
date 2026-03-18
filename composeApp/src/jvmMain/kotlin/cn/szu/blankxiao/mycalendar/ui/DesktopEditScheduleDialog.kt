package cn.szu.blankxiao.mycalendar.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.szu.blankxiao.mycalendar.model.schedule.ScheduleItemData
import cn.szu.blankxiao.mycalendar.ui.component.dialog.AddEditScheduleFormContent
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

/**
 * PC 端编辑日程对话框（复用 AddEditScheduleFormContent）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesktopEditScheduleDialog(
    schedule: ScheduleItemData,
    onDismiss: () -> Unit,
    onConfirm: (ScheduleItemData) -> Unit,
    onDelete: () -> Unit
) {
    var title by remember { mutableStateOf(schedule.title) }
    var description by remember { mutableStateOf(schedule.description) }
    var currentDate by remember { mutableStateOf(schedule.date) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑日程") },
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
                        onConfirm(
                            schedule.copy(
                                title = title,
                                date = currentDate,
                                description = description
                            )
                        )
                    }
                }
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = onDelete) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
                TextButton(onClick = onDismiss) {
                    Text("取消")
                }
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
