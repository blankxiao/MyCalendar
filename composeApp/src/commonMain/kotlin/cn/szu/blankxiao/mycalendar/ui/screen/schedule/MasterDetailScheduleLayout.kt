package cn.szu.blankxiao.mycalendar.ui.screen.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
 * Master-Detail 布局（平板/PC）：左侧列表 + 右侧详情面板
 */
@Composable
fun MasterDetailScheduleLayout(
    schedules: List<ScheduleItemData>,
    selectedSchedule: ScheduleItemData?,
    onScheduleClick: (ScheduleItemData) -> Unit,
    onScheduleToggle: (ScheduleItemData) -> Unit,
    onEditClick: (ScheduleItemData) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(Dp(16f)),
        horizontalArrangement = Arrangement.spacedBy(Dp(16f))
    ) {
        // 左侧：日程列表
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            if (schedules.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
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
                        .fillMaxWidth()
                        .weight(1f),
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

        // 右侧：详情面板
        Column(
            modifier = Modifier
                .width(Dp(320f))
                .fillMaxHeight()
        ) {
            when (val sel = selectedSchedule) {
                null -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "选择日程查看详情",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Dp(8f))
                ) {
                    TextButton(onClick = { onEditClick(sel) }) {
                        Text("编辑")
                    }
                    ScheduleDetailContent(
                        schedule = sel,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
