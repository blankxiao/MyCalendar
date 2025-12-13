package cn.szu.blankxiao.mycalendar.ui.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import cn.szu.blankxiao.mycalendar.ui.theme.outlinedTextFieldColors
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * @author BlankXiao
 * @description 添加日程对话框
 * @date 2025-12-11
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleDialog(
    selectedDate: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (title: String, date: LocalDate, description: String, reminderEnabled: Boolean, reminderTime: LocalDateTime?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf(selectedDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    var reminderEnabled by remember { mutableStateOf(false) }
    
    // 提醒日期时间
    var reminderDate by remember { mutableStateOf(selectedDate) }
    var reminderTime by remember { mutableStateOf(LocalTime.of(9, 0)) } // 默认9:00
    var showReminderDatePicker by remember { mutableStateOf(false) }
    var showReminderTimePicker by remember { mutableStateOf(false) }
    
    val dateFormatter = remember { 
        DateTimeFormatter.ofPattern("yyyy年MM月dd日", Locale.CHINA) 
    }
    val dateTimeFormatter = remember {
        DateTimeFormatter.ofPattern("MM月dd日 HH:mm", Locale.CHINA)
    }
    val customColors = MaterialTheme.customColors
    
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
                // 标题
                Text(
                    text = "添加日程",
                    style = MaterialTheme.typography.titleLarge,
                    color = customColors.calendarNormalText
                )
                
                OutlinedTextField(
                    value = currentDate.format(dateFormatter),
                    onValueChange = { },
                    label = { Text("日期") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    enabled = false,
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "选择日期",
                            modifier = Modifier.clickable { showDatePicker = true }
                        )
                    },
                    colors = outlinedTextFieldColors()
                )
                
                // 标题输入
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("标题") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = outlinedTextFieldColors()
                )
                
                // 描述输入
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("描述（可选）") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    colors = outlinedTextFieldColors()
                )
                
                // 提醒设置
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = reminderEnabled,
                        onCheckedChange = { reminderEnabled = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = customColors.buttonPrimaryBackground,
                            uncheckedColor = customColors.outline,
                            checkmarkColor = customColors.buttonPrimaryText
                        )
                    )
                    Text("启用提醒")
                }
                
                // 提醒日期时间选择
                if (reminderEnabled) {
                    OutlinedTextField(
                        value = LocalDateTime.of(reminderDate, reminderTime).format(dateTimeFormatter),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("提醒时间") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showReminderDatePicker = true },
                        enabled = false,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "设置提醒时间",
                                modifier = Modifier.clickable {
                                    showReminderDatePicker = true 
                                }
                            )
                        },
                        colors = outlinedTextFieldColors()
                    )
                }
                
                // 按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = customColors.textSecondary
                        )
                    ) {
                        Text("取消")
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                // 组合提醒日期时间
                                val reminderDateTime = if (reminderEnabled) {
                                    LocalDateTime.of(reminderDate, reminderTime)
                                } else null
                                
                                onConfirm(
                                    title.trim(),
                                    currentDate,
                                    description.trim(),
                                    reminderEnabled,
                                    reminderDateTime
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
                        Text("添加")
                    }
                }
            }
        }
    }
    
    // 日期选择器对话框
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = currentDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            currentDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
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
    
    // 提醒日期选择器
    if (showReminderDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = reminderDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )
        
        DatePickerDialog(
            onDismissRequest = { showReminderDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            reminderDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showReminderDatePicker = false
                        // 打开时间选择器
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
    
    // 提醒时间选择器
    if (showReminderTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = reminderTime.hour,
            initialMinute = reminderTime.minute,
            is24Hour = true
        )
        
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showReminderTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        reminderTime = LocalTime.of(
                            timePickerState.hour,
                            timePickerState.minute
                        )
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


@Preview
@Composable
private fun PreviewAddScheduleDialog() {
    MyCalendarTheme {
        AddScheduleDialog(LocalDate.now(), {}) {
            title, date, description, reminderEnabled, reminderTime ->
        }
    }
}
