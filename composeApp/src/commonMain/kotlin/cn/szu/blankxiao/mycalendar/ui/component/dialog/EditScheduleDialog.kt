package cn.szu.blankxiao.mycalendar.ui.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import cn.szu.blankxiao.mycalendar.model.schedule.ScheduleItemData
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

/**
 * 编辑日程对话框（复用 AddEditScheduleFormContent）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScheduleDialog(
    scheduleData: ScheduleItemData,
    onDismiss: () -> Unit,
    onConfirm: (id: Long, title: String, date: LocalDate, description: String, reminderEnabled: Boolean, reminderTime: LocalDateTime?) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var title by remember { mutableStateOf(scheduleData.title) }
    var description by remember { mutableStateOf(scheduleData.description) }
    var currentDate by remember { mutableStateOf(scheduleData.date) }
    var showDatePicker by remember { mutableStateOf(false) }

    var reminderEnabled by remember { mutableStateOf(scheduleData.reminderEnabled) }
    var reminderDate by remember {
        mutableStateOf(scheduleData.reminderTime?.date ?: scheduleData.date)
    }
    var reminderHour by remember {
        mutableStateOf(scheduleData.reminderTime?.hour ?: 9)
    }
    var reminderMinute by remember {
        mutableStateOf(scheduleData.reminderTime?.minute ?: 0)
    }
    var showReminderDatePicker by remember { mutableStateOf(false) }
    var showReminderTimePicker by remember { mutableStateOf(false) }
    val customColors = MaterialTheme.customColors
    val timeZone = TimeZone.currentSystemDefault()

    val reminderDateTime = if (reminderEnabled) {
        LocalDateTime(reminderDate.year, reminderDate.monthNumber, reminderDate.dayOfMonth, reminderHour, reminderMinute)
    } else null

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(Dimensions.CornerRadius.large),
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.Padding.medium),
            colors = CardDefaults.cardColors(
                containerColor = customColors.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.Padding.large),
                verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.medium)
            ) {
                Text(
                    text = "编辑日程",
                    style = MaterialTheme.typography.titleLarge,
                    color = customColors.calendarNormalText
                )

                AddEditScheduleFormContent(
                    title = title,
                    onTitleChange = { title = it },
                    description = description,
                    onDescriptionChange = { description = it },
                    date = currentDate,
                    onDateClick = { showDatePicker = true },
                    reminderEnabled = reminderEnabled,
                    onReminderToggle = { reminderEnabled = it },
                    reminderDateTime = reminderDateTime,
                    onReminderClick = { showReminderDatePicker = true },
                    showReminder = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (onDelete != null) {
                        TextButton(
                            onClick = {
                                onDelete()
                                onDismiss()
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = customColors.error
                            )
                        ) {
                            Text("删除")
                        }
                    } else {
                        TextButton(
                            onClick = onDismiss,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = customColors.textSecondary
                            )
                        ) {
                            Text("取消")
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.small)
                    ) {
                        if (onDelete != null) {
                            TextButton(
                                onClick = onDismiss,
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = customColors.textSecondary
                                )
                            ) {
                                Text("取消")
                            }
                        }

                        Button(
                            onClick = {
                                if (title.isNotBlank()) {
                                    val reminder = if (reminderEnabled) reminderDateTime else null
                                    onConfirm(
                                        scheduleData.id,
                                        title.trim(),
                                        currentDate,
                                        description.trim(),
                                        reminderEnabled,
                                        reminder
                                    )
                                    onDismiss()
                                }
                            },
                            enabled = title.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = customColors.buttonPrimaryBackground,
                                contentColor = customColors.buttonPrimaryText
                            )
                        ) {
                            Text("保存")
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = currentDate.atStartOfDayIn(timeZone).toEpochMilliseconds()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            currentDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(timeZone).date
                        }
                        showDatePicker = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = customColors.buttonPrimaryBackground
                    )
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = customColors.textSecondary
                    )
                ) {
                    Text("取消")
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = customColors.surface
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = customColors.surface,
                    titleContentColor = customColors.textPrimary,
                    headlineContentColor = customColors.textPrimary,
                    weekdayContentColor = customColors.textSecondary,
                    navigationContentColor = customColors.textPrimary,
                    yearContentColor = customColors.textPrimary,
                    currentYearContentColor = customColors.buttonPrimaryBackground,
                    selectedYearContentColor = customColors.buttonPrimaryText,
                    selectedYearContainerColor = customColors.buttonPrimaryBackground,
                    dayContentColor = customColors.textPrimary,
                    selectedDayContentColor = customColors.buttonPrimaryText,
                    selectedDayContainerColor = customColors.buttonPrimaryBackground,
                    todayContentColor = customColors.buttonPrimaryBackground,
                    todayDateBorderColor = customColors.buttonPrimaryBackground
                )
            )
        }
    }

    if (showReminderDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = reminderDate.atStartOfDayIn(timeZone).toEpochMilliseconds()
        )
        DatePickerDialog(
            onDismissRequest = { showReminderDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            reminderDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(timeZone).date
                        }
                        showReminderDatePicker = false
                        showReminderTimePicker = true
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = customColors.buttonPrimaryBackground
                    )
                ) {
                    Text("下一步")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showReminderDatePicker = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = customColors.textSecondary
                    )
                ) {
                    Text("取消")
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = customColors.surface
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = customColors.surface,
                    titleContentColor = customColors.textPrimary,
                    headlineContentColor = customColors.textPrimary,
                    weekdayContentColor = customColors.textSecondary,
                    navigationContentColor = customColors.textPrimary,
                    yearContentColor = customColors.textPrimary,
                    currentYearContentColor = customColors.buttonPrimaryBackground,
                    selectedYearContentColor = customColors.buttonPrimaryText,
                    selectedYearContainerColor = customColors.buttonPrimaryBackground,
                    dayContentColor = customColors.textPrimary,
                    selectedDayContentColor = customColors.buttonPrimaryText,
                    selectedDayContainerColor = customColors.buttonPrimaryBackground,
                    todayContentColor = customColors.buttonPrimaryBackground,
                    todayDateBorderColor = customColors.buttonPrimaryBackground
                )
            )
        }
    }

    if (showReminderTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = reminderHour,
            initialMinute = reminderMinute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showReminderTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        reminderHour = timePickerState.hour
                        reminderMinute = timePickerState.minute
                        showReminderTimePicker = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = customColors.buttonPrimaryBackground
                    )
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showReminderTimePicker = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = customColors.textSecondary
                    )
                ) {
                    Text("取消")
                }
            },
            containerColor = customColors.surface,
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = customColors.surfaceVariant,
                        clockDialSelectedContentColor = customColors.buttonPrimaryText,
                        clockDialUnselectedContentColor = customColors.textPrimary,
                        selectorColor = customColors.buttonPrimaryBackground,
                        containerColor = customColors.surface,
                        periodSelectorBorderColor = customColors.outline,
                        periodSelectorSelectedContainerColor = customColors.buttonPrimaryBackground,
                        periodSelectorUnselectedContainerColor = customColors.surface,
                        periodSelectorSelectedContentColor = customColors.buttonPrimaryText,
                        periodSelectorUnselectedContentColor = customColors.textPrimary,
                        timeSelectorSelectedContainerColor = customColors.primaryContainer,
                        timeSelectorUnselectedContainerColor = customColors.surfaceVariant,
                        timeSelectorSelectedContentColor = customColors.buttonPrimaryBackground,
                        timeSelectorUnselectedContentColor = customColors.textPrimary
                    )
                )
            }
        )
    }
}
