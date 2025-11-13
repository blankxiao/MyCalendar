package cn.szu.blankxiao.mycalendar.ui.calendar.specific

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import cn.szu.blankxiao.mycalendar.data.TodoItemData
import cn.szu.blankxiao.mycalendar.ui.calendar.common.DayCell
import cn.szu.blankxiao.mycalendar.ui.calendar.common.DaysOfWeekTitle
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme
import cn.szu.blankxiao.mycalendar.ui.theme.Typography
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.DayPosition
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

private const val TAG = "MonthViewCalendar"

/**
 * @author BlankXiao
 * @description MonthViewCalendar 月视图日历
 * @date 2025-11-06 19:56
 */
@Composable
fun MonthViewCalendar(
	modifier: Modifier = Modifier,
	monthDelta: Long = 100,
	selectedDate: LocalDate,
	date2TodoDataList: Map<LocalDate, List<TodoItemData>>,
	onDateSelected: (LocalDate) -> Unit
) {
	Log.d(TAG, "重组 - selectedDate = $selectedDate")

	// 当前月份
	val currentMonth = remember { YearMonth.now() }
	// 开始月份
	val startMonth = currentMonth.minusMonths(monthDelta)
	// 结束月份
	val endMonth = currentMonth.plusMonths(monthDelta)
	// 协程上下文
	val coroutineScope = rememberCoroutineScope()
	// 组件相关状态
	val state = rememberCalendarState(
		startMonth = startMonth,
		endMonth = endMonth,
		firstDayOfWeek = DayOfWeek.MONDAY,
		firstVisibleMonth = currentMonth
	)

	val customColors = MaterialTheme.customColors

	Column(
		modifier = modifier
			.fillMaxWidth()
			.background(customColors.calendarBackground)
			.padding(Dimensions.Padding.small)
	) {
		MonthTitle(currentMonth = state.firstVisibleMonth.yearMonth, onPreviousMonth = {
			// 协程启动动画
			coroutineScope.launch {
				state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.minusMonths(1))
			}
		}, onNextMouth = {
			// 协程启动动画
			coroutineScope.launch {
				state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.plusMonths(1))
			}
		})

		DaysOfWeekTitle()

		HorizontalCalendar(
			state = state,
			modifier = Modifier.fillMaxWidth(),
			dayContent = { day ->
				val isSelected = day.date == selectedDate

				val currentDayTodo = date2TodoDataList[day.date]
				val showSpot = currentDayTodo != null && !isSelected
				val isCurrentMonth = day.position == DayPosition.MonthDate
				DayCell(
					day = day.date,
					isSelected = isSelected,
					isCurrentMonth = isCurrentMonth,
					hasTodo = showSpot,
					todoDataList = currentDayTodo
				) {
					Log.d(TAG, "点击日期 - ${day.date}")
					onDateSelected(day.date)
				}
			}
		)
	}
}


/**
 * 展示年月份和切换月份的icon
 */
@Composable
fun MonthTitle(
	modifier: Modifier = Modifier,
	currentMonth: YearMonth,
	onPreviousMonth: () -> Unit,
	onNextMouth: () -> Unit
) {
	Row(
		modifier = modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceEvenly
	) {
		// 上一月
		IconButton(onClick = onPreviousMonth) {
			Icon(
				Icons.AutoMirrored.Filled.KeyboardArrowLeft,
				contentDescription = "上一月",
				tint = MaterialTheme.customColors.calendarNavigationIcon
			)
		}
		// 年月份
		Text(
			text = "${currentMonth.year}年${currentMonth.monthValue}月",
			style = Typography.titleMedium,
			color = MaterialTheme.customColors.calendarHeaderText,
			fontWeight = FontWeight.Bold
		)

		// 下一月
		IconButton(onClick = onNextMouth) {
			Icon(
				Icons.AutoMirrored.Filled.KeyboardArrowRight,
				contentDescription = "下一月",
				tint = MaterialTheme.customColors.calendarNavigationIcon
			)
		}
	}
}


@Composable
@Preview(showBackground = true)
fun PreviewMonthViewCalendar() {
	var selectedDate by remember { mutableStateOf(LocalDate.now()) }

	MyCalendarTheme {
		MonthViewCalendar(
			selectedDate = selectedDate,
			date2TodoDataList = mapOf(),
			onDateSelected = { newDate ->
				selectedDate = newDate
			}
		)
	}
}