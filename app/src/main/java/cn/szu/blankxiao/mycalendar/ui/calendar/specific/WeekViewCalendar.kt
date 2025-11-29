package cn.szu.blankxiao.mycalendar.ui.calendar.specific

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import cn.szu.blankxiao.mycalendar.data.todo.exampleTodoItemList
import cn.szu.blankxiao.mycalendar.ui.calendar.common.DayCell
import cn.szu.blankxiao.mycalendar.ui.calendar.common.DaysOfWeekTitle
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme
import cn.szu.blankxiao.mycalendar.ui.theme.Typography
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.Week
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * @author BlankXiao
 * @description WeekViewCalendar
 * @date 2025-11-08 0:00
 */

private const val TAG = "WeekViewCalendar"

@Composable
fun WeekViewCalendar(
	selectedDate: LocalDate,
	modifier: Modifier = Modifier,
	weekDelta: Long = 100,
	onDateSelected: (LocalDate) -> Unit
) {
	val customColors = MaterialTheme.customColors
	val currentDate = remember { LocalDate.now() }
	val startDate = remember { currentDate.minusWeeks(weekDelta) }
	val endDate = remember { currentDate.plusWeeks(weekDelta) }

	val state = rememberWeekCalendarState(
		startDate = startDate,
		endDate = endDate,
		firstVisibleWeekDate = currentDate,
		firstDayOfWeek = DayOfWeek.MONDAY
	)

	val coroutineScope = rememberCoroutineScope()

	Column(
		modifier = modifier
			.fillMaxWidth()
			.fillMaxHeight()
			.background(customColors.calendarBackground)
			.padding(Dimensions.Padding.medium)
	) {
		WeekHeader(state.firstVisibleWeek, onPreviousWeek = {
			coroutineScope.launch {
				state.animateScrollToWeek(state.firstVisibleWeek.days.first().date.minusWeeks(1))
			}
		}, onNextWeek = {
			coroutineScope.launch {
				state.animateScrollToWeek(state.firstVisibleWeek.days.first().date.plusWeeks(1))
			}
		})

		DaysOfWeekTitle()

		WeekCalendar(
			state = state,
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f),
			dayContent = { day ->
			DayCell(day.date, day.date == selectedDate, true, hasTodo = true, todoDataList = exampleTodoItemList) {
				onDateSelected(day.date)
				Log.d(TAG, "WeekViewCalendar: selectedDate新值 $selectedDate")
			}
		})
	}
}

@Composable
fun WeekHeader(
	curWeek: Week, onPreviousWeek: () -> Unit, onNextWeek: () -> Unit, modifier: Modifier = Modifier
) {
	val customColors = MaterialTheme.customColors
	Row(
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.Center,
		verticalAlignment = Alignment.CenterVertically
	) {

		IconButton(onClick = onPreviousWeek) {
			Icon(
				Icons.AutoMirrored.Default.KeyboardArrowLeft,
				"上一周",
				tint = customColors.calendarNavigationIcon
			)
		}

		Text(
			"第${curWeek.days.first().let { it.date.dayOfYear / 7 + 1 }}周",
			style = Typography.headlineMedium,
			fontWeight = FontWeight.Bold
		)

		IconButton(onClick = onNextWeek) {
			Icon(
				Icons.AutoMirrored.Default.KeyboardArrowRight,
				"下一周",
				tint = customColors.calendarNavigationIcon
			)
		}
	}
}

@Preview
@Composable
fun PreviewWeekViewCalendar() {
	var selectedDay by remember { mutableStateOf(LocalDate.now()) }

	MyCalendarTheme {
		WeekViewCalendar(selectedDay) { it ->
			Log.d(TAG, "PreviewWeekViewCalendar: 点击事件 $it")
			selectedDay = it
		}
	}
}


