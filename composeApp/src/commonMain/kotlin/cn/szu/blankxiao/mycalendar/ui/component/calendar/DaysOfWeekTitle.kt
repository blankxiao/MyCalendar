package cn.szu.blankxiao.mycalendar.ui.component.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import cn.szu.blankxiao.mycalendar.ui.Preview
import cn.szu.blankxiao.mycalendar.ui.theme.Typography
import cn.szu.blankxiao.mycalendar.ui.theme.customColors

/**
 * @author BlankXiao
 * @description DaysOfWeekTitle 星期标题
 * @date 2025-11-06 23:26
 */
@Composable
@Preview(showBackground = true)
fun DaysOfWeekTitle(modifier: Modifier = Modifier) {
	Row(
		modifier = modifier
			.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceEvenly,
		verticalAlignment = Alignment.CenterVertically
	) {
		val daysOfWeek = listOf("一", "二", "三", "四", "五", "六", "日")
		daysOfWeek.forEach { day ->
			Text(
				text = day,
				modifier = Modifier.weight(1f),
				style = Typography.titleSmall,
				textAlign = TextAlign.Center,
				color = MaterialTheme.customColors.calendarWeekLabelText
			)
		}
	}
}

