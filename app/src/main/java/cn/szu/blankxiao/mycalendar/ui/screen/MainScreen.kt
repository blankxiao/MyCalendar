package cn.szu.blankxiao.mycalendar.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import cn.szu.blankxiao.mycalendar.data.calendar.CalendarMode
import cn.szu.blankxiao.mycalendar.data.calendar.DayPosition
import cn.szu.blankxiao.mycalendar.data.calendar.rememberCustomCalendarState
import cn.szu.blankxiao.mycalendar.data.schedule.ScheduleItemData
import cn.szu.blankxiao.mycalendar.ui.calendar.common.DayCell
import cn.szu.blankxiao.mycalendar.ui.calendar.specific.AnimatableCustomCalendar
import cn.szu.blankxiao.mycalendar.ui.schedule.ScheduleList
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme
import cn.szu.blankxiao.mycalendar.ui.theme.Typography
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * @author BlankXiao
 * @description 主页面 - 日历与日程
 * @date 2025-12-07 21:04
 */

@Composable
fun MainScreen(
	modifier: Modifier = Modifier
) {
	val coroutineScope = rememberCoroutineScope()
	val customColors = MaterialTheme.customColors

	// 日历范围配置
	val monthDelta = 12L
	val currentMonth = remember { YearMonth.now() }
	val startDate = currentMonth.minusMonths(monthDelta).atDay(1)
	val endDate = currentMonth.plusMonths(monthDelta).atEndOfMonth()

	// 日历状态
	val calendarState = rememberCustomCalendarState(
		startDate = startDate,
		endDate = endDate,
		firstDayOfWeek = DayOfWeek.MONDAY,
		initialVisibleDate = LocalDate.now()
	)

	// 模拟日程数据
	val scheduleList = remember {
		mutableStateListOf(
			ScheduleItemData("团队周会", LocalDate.now(), "讨论本周工作进度", false),
			ScheduleItemData("项目评审", LocalDate.now(), "产品需求评审会议", false),
			ScheduleItemData("健身运动", LocalDate.now(), "晚上7点健身房", false),
			ScheduleItemData("阅读学习", LocalDate.now().plusDays(1), "完成技术书籍第三章", false),
			ScheduleItemData("朋友聚餐", LocalDate.now().plusDays(2), "周末聚餐", false),
			ScheduleItemData("代码优化", LocalDate.now(), "重构日历组件", true)
		)
	}

	// 获取选中日期的日程
	val selectedDateSchedules = remember(calendarState.selectedDate, scheduleList.toList()) {
		scheduleList.filter { it.date == calendarState.selectedDate }
	}

	// 日期格式化
	val dateFormatter = remember {
		DateTimeFormatter.ofPattern("M月d日 EEEE", Locale.CHINA)
	}

	Column(
		modifier = modifier
			.fillMaxSize()
			.background(customColors.calendarBackground)
	) {
		// 顶部标题区域
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(
					horizontal = Dimensions.Padding.large,
					vertical = Dimensions.Padding.medium
				),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			// 当前选中日期
			Text(
				text = calendarState.selectedDate.format(dateFormatter),
				style = Typography.titleLarge,
				fontWeight = FontWeight.Bold,
				color = customColors.calendarNormalText
			)
			// 当前模式
			Text(
				text = "当前模式: ${if (calendarState.calendarMode == CalendarMode.MONTH) "月视图" else "周视图"}",
				style = Typography.bodySmall,
				color = customColors.calendarOtherMonthText
			)
		}

		// 日历组件
		AnimatableCustomCalendar(
			state = calendarState,
			modifier = Modifier.fillMaxWidth()
		) { day ->
			val isSelected = day.date == calendarState.selectedDate
			val isCurrentMonth = day.position == DayPosition.MonthDate

			// 检查该日期是否有日程
			val daySchedules = scheduleList.filter { it.date == day.date }
			val hasSchedule = daySchedules.isNotEmpty() && !isSelected

			DayCell(
				day = day.date,
				isSelected = isSelected,
				isCurrentMonth = isCurrentMonth,
				hasTodo = hasSchedule,
				scheduleDataList = daySchedules.ifEmpty { null },
				modifier = Modifier.weight(1f),
				showScheduleContent = false
			) {
				coroutineScope.launch {
					calendarState.scrollToDate(day.date)
				}
			}
		}

		// 日程列表
		ScheduleList(
			scheduleDataList = selectedDateSchedules,
			title = "${calendarState.selectedDate.dayOfMonth}日 日程",
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f),
			onItemToggle = { item ->
				item.isChecked = !item.isChecked
			}
		)
	}
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun PreviewMainScreen() {
	MyCalendarTheme {
		MainScreen()
	}
}
