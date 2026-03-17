package cn.szu.blankxiao.mycalendar.ui.component.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cn.szu.blankxiao.mycalendar.model.schedule.ScheduleItemData

/**
 * 日程卡片（共享组件）
 * 展示日程标题、描述、完成状态，支持点击和勾选
 */
@Composable
fun ScheduleItemCard(
    itemData: ScheduleItemData,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onCheckedChange: () -> Unit = {},
    showCheckbox: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 状态指示器
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = if (itemData.isChecked) colorScheme.primary else colorScheme.tertiary,
                        shape = RoundedCornerShape(50)
                    )
            )

            // 内容区域
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = itemData.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (itemData.isChecked) FontWeight.Normal else FontWeight.Medium,
                    color = if (itemData.isChecked) colorScheme.onSurfaceVariant else colorScheme.onSurface,
                    textDecoration = if (itemData.isChecked) TextDecoration.LineThrough else TextDecoration.None
                )
                if (itemData.description.isNotBlank()) {
                    Text(
                        text = itemData.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            // 完成复选框
            if (showCheckbox) {
                Checkbox(
                    checked = itemData.isChecked,
                    onCheckedChange = { onCheckedChange() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = colorScheme.primary,
                        uncheckedColor = colorScheme.outline,
                        checkmarkColor = colorScheme.onPrimary
                    )
                )
            }
        }
    }
}
