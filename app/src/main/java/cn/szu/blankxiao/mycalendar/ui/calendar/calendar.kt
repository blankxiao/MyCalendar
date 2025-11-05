package cn.szu.blankxiao.mycalendar.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.Week
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun FlexibleCalendar(
	modifier: Modifier = Modifier,
	onDateSelected: (LocalDate) -> Unit = {}
) {
	var viewType by remember { mutableStateOf(CalendarViewType.MONTH) }
	var selectedDate by remember { mutableStateOf(LocalDate.now()) }

	Column(
		modifier = modifier
			.fillMaxWidth()
			.background(Color.White)
	) {
		// 视图切换按钮
		ViewTypeSelector(
			currentViewType = viewType,
			onViewTypeChange = { viewType = it }
		)

		HorizontalDivider()

		// 根据视图类型显示不同的日历
		when (viewType) {
			CalendarViewType.DAY -> DayViewCalendar(
				selectedDate = selectedDate,
				onDateSelected = {
					selectedDate = it
					onDateSelected(it)
				}
			)
			CalendarViewType.WEEK -> WeekViewCalendar(
				selectedDate = selectedDate,
				onDateSelected = {
					selectedDate = it
					onDateSelected(it)
				}
			)
			CalendarViewType.MONTH -> MonthViewCalendar(
				selectedDate = selectedDate,
				onDateSelected = {
					selectedDate = it
					onDateSelected(it)
				}
			)
		}
	}
}

/**
 * 视图类型选择器
 */
@Composable
private fun ViewTypeSelector(
	currentViewType: CalendarViewType,
	onViewTypeChange: (CalendarViewType) -> Unit
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(16.dp),
		horizontalArrangement = Arrangement.Center,
		verticalAlignment = Alignment.CenterVertically
	) {
		CalendarViewType.entries.forEach { type ->
			val isSelected = currentViewType == type

			Button(
				onClick = { onViewTypeChange(type) },
				colors = ButtonDefaults.buttonColors(
					containerColor = if (isSelected) Color(0xFF6200EE) else Color.LightGray,
					contentColor = if (isSelected) Color.White else Color.DarkGray
				),
				modifier = Modifier.padding(horizontal = 4.dp)
			) {
				Text(
					text = when (type) {
						CalendarViewType.DAY -> "日"
						CalendarViewType.WEEK -> "周"
						CalendarViewType.MONTH -> "月"
					}
				)
			}
		}
	}
}

/**
 * 月视图
 */
@Composable
private fun MonthViewCalendar(
	selectedDate: LocalDate,
	onDateSelected: (LocalDate) -> Unit
) {
	val currentMonth = remember { YearMonth.now() }
	val startMonth = remember { currentMonth.minusMonths(100) }
	val endMonth = remember { currentMonth.plusMonths(100) }

	val coroutineScope = rememberCoroutineScope()

	val state = rememberCalendarState(
		startMonth = startMonth,
		endMonth = endMonth,
		firstVisibleMonth = currentMonth,
		firstDayOfWeek = DayOfWeek.MONDAY
	)

	Column {
		// 月份标题和导航
		MonthHeader(
			currentMonth = state.firstVisibleMonth.yearMonth,
			onPreviousMonth = {
				coroutineScope.launch {
					state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.minusMonths(1)) }
				},
			onNextMonth = {
				coroutineScope.launch {
					state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.plusMonths(1))
				}
			}
		)

		// 星期标题
		DaysOfWeekTitle()

		HorizontalCalendar(
			modifier = Modifier.fillMaxWidth(),
			state = state,
			dayContent = { day ->
				DayCell(
					day = day.date,
					isSelected = day.date == selectedDate,
					isCurrentMonth = day.position == DayPosition.MonthDate,
					onClick = { onDateSelected(day.date) }
				)
			}
		)
	}
}

/**
 * 周视图
 */
@Composable
private fun WeekViewCalendar(
	selectedDate: LocalDate,
	onDateSelected: (LocalDate) -> Unit
) {
	val currentDate = remember { LocalDate.now() }
	val startDate = remember { currentDate.minusWeeks(100) }
	val endDate = remember { currentDate.plusWeeks(100) }

	val state = rememberWeekCalendarState(
		startDate = startDate,
		endDate = endDate,
		firstVisibleWeekDate = currentDate,
		firstDayOfWeek = DayOfWeek.MONDAY
	)

	Column {
		// 周标题
		WeekHeader(
			currentWeek = state.firstVisibleWeek,
			onPreviousWeek = { /* 实现上一周 */ },
			onNextWeek = { /* 实现下一周 */ }
		)

		// 星期标题
		DaysOfWeekTitle()

		WeekCalendar(
			modifier = Modifier.fillMaxWidth(),
			state = state,
			dayContent = { day ->
				DayCell(
					day = day.date,
					isSelected = day.date == selectedDate,
					isCurrentMonth = true,
					onClick = { onDateSelected(day.date) }
				)
			}
		)
	}
}

/**
 * 日视图
 */
@Composable
private fun DayViewCalendar(
	selectedDate: LocalDate,
	onDateSelected: (LocalDate) -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(16.dp)
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			IconButton(onClick = { onDateSelected(selectedDate.minusDays(1)) }) {
				Icon(Icons.Default.KeyboardArrowLeft, "上一天")
			}

			Column(horizontalAlignment = Alignment.CenterHorizontally) {
				Text(
					text = "${selectedDate.year}年${selectedDate.monthValue}月",
					fontSize = 18.sp,
					fontWeight = FontWeight.Bold
				)
				Text(
					text = selectedDate.dayOfMonth.toString(),
					fontSize = 48.sp,
					fontWeight = FontWeight.Bold,
					color = Color(0xFF6200EE)
				)
				Text(
					text = selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.CHINA),
					fontSize = 16.sp,
					color = Color.Gray
				)
			}

			IconButton(onClick = { onDateSelected(selectedDate.plusDays(1)) }) {
				Icon(Icons.Default.KeyboardArrowRight, "下一天")
			}
		}
	}
}

/**
 * 月份标题
 */
@Composable
private fun MonthHeader(
	currentMonth: YearMonth,
	onPreviousMonth: () -> Unit,
	onNextMonth: () -> Unit
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(16.dp),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		IconButton(onClick = onPreviousMonth) {
			Icon(Icons.Default.KeyboardArrowLeft, "上一月")
		}

		Text(
			text = "${currentMonth.year}年${currentMonth.monthValue}月",
			fontSize = 18.sp,
			fontWeight = FontWeight.Bold
		)

		IconButton(onClick = onNextMonth) {
			Icon(Icons.Default.KeyboardArrowRight, "下一月")
		}
	}
}

/**
 * 周标题
 */
@Composable
private fun WeekHeader(
	currentWeek: Week,
	onPreviousWeek: () -> Unit,
	onNextWeek: () -> Unit
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(16.dp),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		IconButton(onClick = onPreviousWeek) {
			Icon(Icons.Default.KeyboardArrowLeft, "上一周")
		}

		Text(
			text = "第${currentWeek.days.first().date.let {
				it.dayOfYear / 7 + 1
			}}周",
			fontSize = 18.sp,
			fontWeight = FontWeight.Bold
		)

		IconButton(onClick = onNextWeek) {
			Icon(Icons.Default.KeyboardArrowRight, "下一周")
		}
	}
}

/**
 * 星期标题行
 */
@Composable
private fun DaysOfWeekTitle() {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp)
	) {
		val daysOfWeek = listOf("一", "二", "三", "四", "五", "六", "日")
		daysOfWeek.forEach { day ->
			Text(
				text = day,
				modifier = Modifier.weight(1f),
				textAlign = TextAlign.Center,
				fontSize = 14.sp,
				color = Color.Gray,
				fontWeight = FontWeight.Medium
			)
		}
	}
}

/**
 * 日期单元格
 */
@Composable
private fun DayCell(
	day: LocalDate,
	isSelected: Boolean,
	isCurrentMonth: Boolean,
	onClick: () -> Unit
) {
	val isToday = day == LocalDate.now()

	Box(
		modifier = Modifier
			.aspectRatio(1f)
			.padding(4.dp)
			.clip(CircleShape)
			.background(
				when {
					isSelected -> Color(0xFF6200EE)
					isToday -> Color(0xFFE3F2FD)
					else -> Color.Transparent
				}
			)
			.border(
				width = if (isToday && !isSelected) 1.dp else 0.dp,
				color = Color(0xFF6200EE),
				shape = CircleShape
			)
			.clickable(onClick = onClick),
		contentAlignment = Alignment.Center
	) {
		Text(
			text = day.dayOfMonth.toString(),
			fontSize = 14.sp,
			color = when {
				isSelected -> Color.White
				!isCurrentMonth -> Color.LightGray
				isToday -> Color(0xFF6200EE)
				else -> Color.Black
			},
			fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
		)
	}
}

@Preview(showBackground = true)
@Composable
fun PreviewFlexibleCalendar() {
	FlexibleCalendar()
}