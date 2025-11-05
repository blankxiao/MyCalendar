package cn.szu.blankxiao.mycalendar.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * @author BlankXiao
 * @description CalendarTypeSelector
 * @date 2025-11-04 21:09
 */

val btnSelectedColor = 0xFF6200EE

@Composable()
fun CalendarTypeSelector(
	selected: CalendarViewType = CalendarViewType.MONTH,
	onViewTypeChange: () -> Unit,
	modifier: Modifier = Modifier
) {

	Row(
		modifier = modifier
			.fillMaxWidth()
			.padding(16.dp),
		horizontalArrangement = Arrangement.Center,
		verticalAlignment = Alignment.CenterVertically
	) {
		CalendarViewType.entries.forEach { type ->
			val isSelected = type == selected

			Button(
				onClick = onViewTypeChange,
				colors = ButtonDefaults.buttonColors(
					containerColor = if (isSelected) Color(btnSelectedColor) else Color.LightGray,
					contentColor = if (isSelected) Color.White else Color.DarkGray
				),
				modifier = Modifier.padding(4.dp)
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


@Preview(showBackground = true)
@Composable
fun PreviewCalendarTypeSelector() {
	CalendarTypeSelector(onViewTypeChange = {})
}
