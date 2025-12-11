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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import cn.szu.blankxiao.mycalendar.data.schedule.ScheduleItemData
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * @author BlankXiao
 * @description 编辑日程对话框
 * @date 2025-12-11
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScheduleDialog(
    scheduleData: ScheduleItemData,
    onDismiss: () -> Unit,
    onConfirm: (id: Long, title: String, date: LocalDate, description: String) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var title by remember { mutableStateOf(scheduleData.title) }
    var description by remember { mutableStateOf(scheduleData.desc) }
    var currentDate by remember { mutableStateOf(scheduleData.date) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val dateFormatter = remember { 
        DateTimeFormatter.ofPattern("yyyy年MM月dd日", Locale.CHINA) 
    }
    val customColors = MaterialTheme.customColors
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(Dimensions.CornerRadius.large),
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.Padding.medium)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.Padding.large),
                verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.medium)
            ) {
                // 标题
                Text(
                    text = "编辑日程",
                    style = MaterialTheme.typography.titleLarge,
                    color = customColors.calendarNormalText
                )
                
                // 日期选择（可点击）
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
                    colors = androidx.compose.material3.TextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                
                // 标题输入
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("标题") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // 描述输入
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("描述（可选）") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
                
                // 按钮行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 删除按钮（如果提供）
                    if (onDelete != null) {
                        TextButton(
                            onClick = {
                                onDelete()
                                onDismiss()
                            }
                        ) {
                            Text("删除", color = MaterialTheme.colorScheme.error)
                        }
                    } else {
                        TextButton(onClick = onDismiss) {
                            Text("取消")
                        }
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.small)
                    ) {
                        if (onDelete != null) {
                            TextButton(onClick = onDismiss) {
                                Text("取消")
                            }
                        }
                        
                        Button(
                            onClick = {
                                if (title.isNotBlank()) {
                                    onConfirm(
                                        scheduleData.id,
                                        title.trim(),
                                        currentDate,
                                        description.trim()
                                    )
                                    onDismiss()
                                }
                            },
                            enabled = title.isNotBlank()
                        ) {
                            Text("保存")
                        }
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
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

