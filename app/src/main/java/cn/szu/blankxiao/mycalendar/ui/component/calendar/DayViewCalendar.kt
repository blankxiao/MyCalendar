package cn.szu.blankxiao.mycalendar.ui.component.calendar

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme
import cn.szu.blankxiao.mycalendar.ui.theme.Typography
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

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

		IconButton(onClick = { onDateSelected(selectedDate.minusDays(1)) }) {
			Icon(
				Icons.AutoMirrored.Filled.KeyboardArrowLeft, "前一天",
				tint = customColors.calendarNavigationIcon
			)
		}

		Column(
			modifier = Modifier,
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				"${selectedDate.year}年${selectedDate.month.value}月",
				style = Typography.titleMedium,
				color = customColors.calendarHeaderText
			)
			Text(
				"${selectedDate.dayOfMonth}",
				style = Typography.displayMedium,
				fontWeight = FontWeight.Bold
			)
			Text(
				"${selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.CHINA)}",
				style = Typography.bodyMedium,
				color = customColors.calendarWeekLabelText
			)
		}

		IconButton(onClick = { onDateSelected(selectedDate.plusDays(1)) }) {
			Icon(
				Icons.AutoMirrored.Filled.KeyboardArrowRight, "后一天",
				tint = customColors.calendarNavigationIcon
			)
		}
	}
}

@Composable
@Preview
fun PreviewDayViewCalendar() {
	var selectedDate by remember { mutableStateOf(LocalDate.now()) }

	MyCalendarTheme {
		DayViewCalendar(selectedDate = selectedDate) { date ->
			selectedDate = date
		}
	}
}

