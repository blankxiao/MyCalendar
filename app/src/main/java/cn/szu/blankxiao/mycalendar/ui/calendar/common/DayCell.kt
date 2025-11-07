package cn.szu.blankxiao.mycalendar.ui.calendar.common

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.Typography
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import java.time.LocalDate

private const val TAG = "DayCell"


/**
 * @author BlankXiao
 * @description DayCell 日期单天的样式
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
	val customColors = MaterialTheme.customColors
	
	Log.d(TAG, "渲染 - day=$day, isSelected=$isSelected, isToday=$isToday")

	Box(
		modifier = Modifier
			.aspectRatio(1f)
			.padding(Dimensions.Padding.tiny)
			.clip(CircleShape)
			.background(
				when {
					isSelected -> customColors.calendarSelectedBackground
					isToday -> customColors.calendarTodayBackground
					else -> Color.Transparent
				}
			)
			.border(
				width = if (isToday && !isSelected) 1.dp else 0.dp,
				color = customColors.calendarDivider,
				shape = CircleShape
			)
			.clickable {
				Log.d(TAG, "DayCell 点击 - day=$day")
				onClick()
			},
		contentAlignment = Alignment.Center
	) {
		Text(
			text = day.dayOfMonth.toString(),
			style = Typography.bodyLarge,
			fontWeight = FontWeight.Bold,
			color = when {
				isSelected -> customColors.calendarSelectedText
				isToday -> customColors.calendarTodayText
				!isCurrentMonth -> customColors.calendarOtherMonthText
				else -> customColors.calendarNormalText
			}
		)
	}
}

@Composable
@Preview(showBackground = true)
fun PreviewDayCell() {
	DayCell(LocalDate.now(), isSelected = false, isCurrentMonth = true) { }
}