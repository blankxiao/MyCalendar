package cn.szu.blankxiao.mycalendar.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cn.szu.blankxiao.mycalendar.ui.calendar.common.CalendarViewType
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import java.time.LocalDate

/**
 * @author BlankXiao
 * @description CustomCalendarView
 * @date 2025-11-08 11:46
 */

private const val TAG = "CustomCalendarView"

@Composable
fun CustomCalendarView(
	modifier: Modifier = Modifier,
	selectedDate: LocalDate,
	onDateSelected: (LocalDate) -> Unit
) {
	val customColors = MaterialTheme.customColors
	var currentViewType by remember { mutableStateOf(CalendarViewType.MONTH) }
	Column(
		modifier = modifier
			.fillMaxWidth()
			.background(customColors.calendarBackground),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		TypeViewSelector(currentViewType = currentViewType) {
			currentViewType = it
		}

		HorizontalDivider(
			thickness = Dimensions.Divider.thickness,
			color = customColors.calendarDivider
		)

	}

}

@Composable
fun TypeViewSelector(
	modifier: Modifier = Modifier,
	currentViewType: CalendarViewType,
	onSelectType: (CalendarViewType) -> Unit
) {
	val customColors = MaterialTheme.customColors
	Row(
		modifier = modifier.padding(Dimensions.Padding.medium),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.Center
	) {
		CalendarViewType.entries.forEach {
			val isSelected = it == currentViewType
			Button(
				onClick = { onSelectType(it) },
				modifier = modifier.padding(horizontal = Dimensions.Padding.tiny),
				colors = ButtonDefaults.buttonColors(
					contentColor = if (isSelected) customColors.buttonPrimaryText else customColors.buttonSecondaryText,
					containerColor = if (isSelected) customColors.buttonPrimaryBackground else customColors.buttonSecondaryBackground,
				)
			) {
				Text(it.name)
			}
		}
	}
}


@Preview
@Composable
fun PreviewCustomCalendarView() {
	var selectedDate by remember { mutableStateOf(LocalDate.now()) }
	MyCalendarTheme {
		CustomCalendarView(selectedDate = selectedDate) {
			selectedDate = it
		}
	}
}


