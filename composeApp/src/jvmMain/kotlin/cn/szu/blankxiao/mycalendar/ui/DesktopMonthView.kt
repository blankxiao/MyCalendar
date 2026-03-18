package cn.szu.blankxiao.mycalendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.szu.blankxiao.mycalendar.model.calendar.DayPosition
import cn.szu.blankxiao.mycalendar.model.schedule.ScheduleItemData
import cn.szu.blankxiao.mycalendar.util.CalendarDataCalculator
import cn.szu.blankxiao.mycalendar.util.formatForDisplay
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import cn.szu.blankxiao.mycalendar.model.calendar.OutDateStyle
import cn.szu.blankxiao.mycalendar.model.calendar.YearMonth
import cn.szu.blankxiao.mycalendar.ui.theme.customColors

/**
 * PC 端月历网格视图
 */
@Composable
fun DesktopMonthView(
    yearMonth: YearMonth,
    allSchedules: List<ScheduleItemData>,
    selectedDate: LocalDate?,
    onDateClick: (LocalDate) -> Unit,
    onScheduleClick: (ScheduleItemData) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val calendarData = remember(yearMonth) {
        CalendarDataCalculator.calculateMonth(
            yearMonth = yearMonth,
            firstDayOfWeek = DayOfWeek.SUNDAY,
            outDateStyle = OutDateStyle.EndOfGrid
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        // 星期标题
        val customColors = MaterialTheme.customColors
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("周日", "周一", "周二", "周三", "周四", "周五", "周六").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodyMedium,
                    color = customColors.calendarWeekLabelText,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 日期网格
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(
                items = calendarData.weeks.flatMap { it.days },
                key = { it.date.toString() }
            ) { day ->
                val daySchedules = allSchedules.filter { it.date == day.date }
                val isToday = day.date == today
                val isOutMonth = day.position != DayPosition.MonthDate

                DayCell(
                    date = day.date,
                    schedules = daySchedules,
                    isToday = isToday,
                    isOutMonth = isOutMonth,
                    isSelected = day.date == selectedDate,
                    onClick = { onDateClick(day.date) },
                    onScheduleClick = onScheduleClick
                )
            }
        }
    }
}

/** 每个日期格子的固定高度，保证网格整齐，加高以容纳更多日程条 */
private val DayCellHeight = 100.dp

@Composable
private fun DayCell(
    date: LocalDate,
    schedules: List<ScheduleItemData>,
    isToday: Boolean,
    isOutMonth: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onScheduleClick: (ScheduleItemData) -> Unit
) {
    val customColors = MaterialTheme.customColors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(DayCellHeight)
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = when {
                    isSelected -> customColors.calendarSelectedBackground.copy(alpha = 0.15f)
                    else -> MaterialTheme.colorScheme.surface
                }
            )
            .border(
                width = 2.dp,
                color = if (isSelected) customColors.calendarSelectedBackground else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(6.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 日期数字
            val dateModifier = if (isToday) Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(customColors.calendarTodayBackground)
                .border(1.dp, customColors.calendarTodayBorder, CircleShape)
                .padding(4.dp)
            else Modifier
            Box(
                modifier = dateModifier,
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = date.day.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                        isToday -> customColors.calendarTodayText
                        isOutMonth -> customColors.calendarOtherMonthText
                        else -> customColors.calendarNormalText
                    },
                    fontSize = if (isToday) 14.sp else 13.sp
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // 日程胶囊：紧凑显示，超出时最后一条显示「...」表示还有更多
            val maxVisible = 3
            val displaySchedules = schedules.take(maxVisible)
            val hasMore = schedules.size > maxVisible
            displaySchedules.forEachIndexed { index, schedule ->
                val isLastAndMore = hasMore && index == displaySchedules.lastIndex
                SchedulePill(
                    title = if (isLastAndMore) "..." else schedule.title,
                    onClick = { if (!isLastAndMore) onScheduleClick(schedule) },
                    isMoreIndicator = isLastAndMore
                )
                Spacer(modifier = Modifier.height(1.dp))
            }
        }
    }
}

@Composable
private fun SchedulePill(
    title: String,
    onClick: () -> Unit,
    isMoreIndicator: Boolean = false
) {
    val customColors = MaterialTheme.customColors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(
                if (isMoreIndicator) customColors.surfaceVariant
                else customColors.primaryContainer
            )
            .then(
                if (isMoreIndicator) Modifier
                else Modifier.clickable(onClick = onClick)
            )
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = if (isMoreIndicator) customColors.onSurfaceVariant
                else customColors.onPrimaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
