package cn.szu.blankxiao.mycalendar.ui.component.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import cn.szu.blankxiao.mycalendar.ui.theme.outlinedTextFieldColors
import cn.szu.blankxiao.mycalendar.util.formatForDisplay
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

/**
 * 添加/编辑日程表单内容（共享组件）
 * 包含标题、描述、日期、提醒等字段，不包含日期/时间选择器（由父组件处理）
 * @param showReminder 是否显示提醒区块，Desktop 可传 false
 */
@Composable
fun AddEditScheduleFormContent(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    date: LocalDate,
    onDateClick: () -> Unit,
    reminderEnabled: Boolean,
    onReminderToggle: (Boolean) -> Unit,
    reminderDateTime: LocalDateTime?,
    onReminderClick: () -> Unit,
    showReminder: Boolean = true,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dp(16f))
    ) {
        val textFieldColors = outlinedTextFieldColors()

        // 日期选择（可点击）
        OutlinedTextField(
            value = date.formatForDisplay("yyyy年MM月dd日"),
            onValueChange = { },
            label = { Text("日期") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDateClick() },
            enabled = false,
            readOnly = true,
            colors = textFieldColors,
            trailingIcon = {
                TextButton(onClick = onDateClick) {
                    Text("选择", style = MaterialTheme.typography.labelMedium)
                }
            }
        )

        // 标题输入
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("标题") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = textFieldColors
        )

        // 描述输入
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("描述（可选）") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            colors = textFieldColors
        )

        // 提醒设置（仅当 showReminder 为 true 时显示）
        if (showReminder) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = reminderEnabled,
                    onCheckedChange = onReminderToggle,
                    colors = CheckboxDefaults.colors(
                        checkedColor = colorScheme.primary,
                        uncheckedColor = colorScheme.outline,
                        checkmarkColor = colorScheme.onPrimary
                    )
                )
                Text("启用提醒")
            }

            // 提醒日期时间选择
            if (reminderEnabled) {
                OutlinedTextField(
                    value = reminderDateTime?.formatForDisplay("MM月dd日 HH:mm") ?: "",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("提醒时间") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onReminderClick() },
                    enabled = false,
                    colors = textFieldColors,
                    trailingIcon = {
                        TextButton(onClick = onReminderClick) {
                            Text("设置", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                )
            }
        }
    }
}
