package cn.szu.blankxiao.mycalendar.ui.calendar.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

/**
 * @author BlankXiao
 * @description DayCell
 * @date 2025-11-04 21:36
 */
@Composable()
fun DayCell(
	day: LocalDate,
	isSelected: Boolean,
	isCurrentMonth: Boolean,
	onClick: () -> Unit,
) {
	val isToday = day == LocalDate.now()

	Box(
		modifier = Modifier
			.aspectRatio(1f)
			// TODO 选择时改变
			.border(width = 1.dp, color = Color.Black)
			.clip(CircleShape)
			.padding(4.dp)
			// TODO isCurrentMonth
			.background(when {
				isSelected -> Color(0xFF6200EE)
				isToday -> Color(0xFFE3F2FD)
				else -> Color.Transparent
			})
			.clickable { onClick },
		contentAlignment = Alignment.Center
	) {
		Text(
			text = day.dayOfMonth.toString(),
			fontSize = 14.sp,
			// TODO 选择时改变
			color = Color.Black,
			fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
		)

	}
}

@Composable
@Preview(showBackground = true)
fun PreviewDayCell() {
	DayCell(LocalDate.now(), isSelected = false, isCurrentMonth = true) { }
}