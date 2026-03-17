package cn.szu.blankxiao.mycalendar.ui.screen.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import cn.szu.blankxiao.mycalendar.model.schedule.ScheduleItemData
import cn.szu.blankxiao.mycalendar.ui.component.schedule.ScheduleDetailContent
import cn.szu.blankxiao.mycalendar.ui.component.schedule.ScheduleItemCard

/**
 * 紧凑布局（手机）：日程列表 + 全屏详情
 * 点击日程项 → 全屏显示详情，带返回
 */
@Composable
fun CompactScheduleLayout(
    schedules: List<ScheduleItemData>,
    selectedSchedule: ScheduleItemData?,
    onScheduleClick: (ScheduleItemData) -> Unit,
    onScheduleToggle: (ScheduleItemData) -> Unit,
    onEditClick: (ScheduleItemData) -> Unit,
    onSelectSchedule: (ScheduleItemData?) -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedSchedule != null) {
        // 全屏详情
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(Dp(16f))
        ) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { onSelectSchedule(null) }) {
                    Text("← 返回")
                }
                TextButton(onClick = { onEditClick(selectedSchedule) }) {
                    Text("编辑")
                }
            }
            ScheduleDetailContent(
                schedule = selectedSchedule,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    } else {
        // 列表
        if (schedules.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dp(16f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无日程，点击右下角 + 添加",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dp(16f)),
                verticalArrangement = Arrangement.spacedBy(Dp(8f))
            ) {
                items(schedules, key = { it.id }) { item ->
                    ScheduleItemCard(
                        itemData = item,
                        onClick = { onScheduleClick(item) },
                        onCheckedChange = { onScheduleToggle(item) }
                    )
                }
            }
        }
    }
}
