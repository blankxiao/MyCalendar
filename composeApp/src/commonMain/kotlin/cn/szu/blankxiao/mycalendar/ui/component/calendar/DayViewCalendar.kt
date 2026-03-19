package cn.szu.blankxiao.mycalendar.ui.component.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import cn.szu.blankxiao.mycalendar.ui.Preview
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme
import cn.szu.blankxiao.mycalendar.ui.theme.Typography
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import cn.szu.blankxiao.mycalendar.util.formatForDisplay
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

/**
 * @author BlankXiao
 * @description DayViewCalendar
 * @date 2025-11-08 1:47
 */

private const val TAG = "DayViewCalendar"

@Composable
fun DayViewCalendar(
	modifier: Modifier = Modifier,
	selectedDate: LocalDate,
	onDateSelected: (LocalDate) -> Unit,
) {
	val customColors = MaterialTheme.customColors

	Row(
		modifier = modifier
			.fillMaxWidth()
			.background(customColors.calendarBackground)
			.padding(Dimensions.Padding.medium),
		horizontalArrangement = Arrangement.SpaceEvenly,
		verticalAlignment = Alignment.CenterVertically
	) {

		IconButton(onClick = { onDateSelected(selectedDate.plus(-1, DateTimeUnit.DAY)) }) {
			Icon(
				Icons.Filled.KeyboardArrowLeft, "前一天",
				tint = customColors.calendarNavigationIcon
			)
		}

		Column(
			modifier = Modifier,
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				"${selectedDate.year}年${selectedDate.monthNumber}月",
				style = Typography.titleMedium,
				color = customColors.calendarHeaderText
			)
			Text(
				"${selectedDate.dayOfMonth}",
				style = Typography.displayMedium,
				fontWeight = FontWeight.Bold
			)
			Text(
				selectedDate.formatForDisplay("EEEE"),
				style = Typography.bodyMedium,
				color = customColors.calendarWeekLabelText
			)
		}

		IconButton(onClick = { onDateSelected(selectedDate.plus(1, DateTimeUnit.DAY)) }) {
			Icon(
				Icons.Filled.KeyboardArrowRight, "后一天",
				tint = customColors.calendarNavigationIcon
			)
		}
	}
}

@Composable
@Preview
fun PreviewDayViewCalendar() {
	var selectedDate by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date) }

	MyCalendarTheme {
		DayViewCalendar(selectedDate = selectedDate) { date ->
			selectedDate = date
		}
	}
}

