package cn.szu.blankxiao.mycalendar.ui.calendar.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import cn.szu.blankxiao.mycalendar.data.schedule.ScheduleItemData
import cn.szu.blankxiao.mycalendar.data.schedule.exampleScheduleItemList
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme
import cn.szu.blankxiao.mycalendar.ui.theme.Typography
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import cn.szu.blankxiao.mycalendar.utils.LunarUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

private const val TAG = "DayCell"


/**
 * @author BlankXiao
 * @description DayCell 日期单天的样式
 * @date 2025-11-04 21:36
 */
@Composable
fun DayCell(
	day: LocalDate,
	isSelected: Boolean,
	isCurrentMonth: Boolean,
	hasTodo: Boolean,
	scheduleDataList: List<ScheduleItemData>?,
	modifier: Modifier = Modifier,
	showScheduleContent: Boolean = false,
	onClick: () -> Unit,
) {
	val customColors = MaterialTheme.customColors
	
	val today = remember { LocalDate.now() }
	val isToday = day == today

	val textColor = remember(isSelected, isToday, isCurrentMonth, customColors) {
		when {
			isSelected -> customColors.calendarSelectedText
			isToday -> customColors.calendarTodayText
			!isCurrentMonth -> customColors.calendarOtherMonthText
			else -> customColors.calendarNormalText
		}
	}
	
	val backgroundColor = remember(isSelected, isToday, customColors) {
		when {
			isSelected -> customColors.calendarSelectedBackground
			isToday -> customColors.calendarTodayBackground
			else -> Color.Transparent
		}
	}
	
	// 农历文本 - 延迟加载，先显示公历，农历异步计算后显示
	val lunarText by produceState(initialValue = "", key1 = day) {
		value = withContext(Dispatchers.Default) {
			LunarUtil.getLunarDayText(day)
		}
	}

	// 外层 Column：包含圆形日期区域 + Schedule 内容
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Top,
		modifier = modifier
	) {
		// 圆形日期区域
		Box(
			modifier = Modifier
				.aspectRatio(1f)
				.padding(Dimensions.Padding.tiny)
				.clip(CircleShape)
				.background(backgroundColor)
				.border(
					width = if (isToday && !isSelected) Dimensions.Divider.thickness else Dimensions.Size.extraTiny,
					color = if (isToday && !isSelected) customColors.calendarTodayBorder else Color.Transparent,
					shape = CircleShape
				)
				.clickable(onClick = onClick),
			contentAlignment = Alignment.Center
		) {
			// 待办指示点（绝对定位在顶部）
			if (hasTodo && !isSelected) {
				Box(
					modifier = Modifier
						.align(Alignment.TopCenter)
						.offset(y = Dimensions.Spacing.tiny)
						.size(Dimensions.Size.tiny)
						.clip(CircleShape)
						.background(customColors.calendarScheduleDot)
				)
			}

			// 公历和农历垂直排列
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Center
			) {
				// 公历日期
				Text(
					text = day.dayOfMonth.toString(),
					style = Typography.titleSmall,
					fontWeight = FontWeight.Bold,
					color = textColor
				)

				Text(
					text = lunarText.ifEmpty { "　" },
					style = Typography.labelSmall,
					color = if (lunarText.isNotEmpty()) textColor.copy(alpha = 0.6f) else Color.Transparent
				)
			}
		}

		// ScheduleData 内容（在圆圈外部）
		if (showScheduleContent && !scheduleDataList.isNullOrEmpty()) {
			Column(
				modifier = Modifier
					.padding(top = Dimensions.Padding.tiny)
					.padding(horizontal = Dimensions.Padding.tiny),
				verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.tiny),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				scheduleDataList.take(2).forEach { scheduleItem ->
					simpleScheduleItem(scheduleItem, textColor)
				}
				
				// 如果还有更多 Schedule，显示提示
				if (scheduleDataList.size > 2) {
					Text(
						text = "📝 还有 ${scheduleDataList.size - 2} 项",
						style = Typography.labelSmall,
						fontSize = 8.sp,
						color = textColor.copy(alpha = 0.5f)
					)
				}
			}
		}
	}
}

@Composable
private fun simpleScheduleItem(
	scheduleItemData: ScheduleItemData,
	textColor: Color
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.Center,
		modifier = Modifier.fillMaxWidth()
	) {
		// 表情图标（根据完成状态）
		Text(
			text = if (scheduleItemData.isChecked) "✅" else "📌",
			style = Typography.labelSmall,
			fontSize = 10.sp
		)

		// Schedule 标题（限制长度）
		Text(
			text = scheduleItemData.title.take(4),  // 最多显示 4 个字
			style = Typography.labelSmall,
			color = if (scheduleItemData.isChecked)
				textColor.copy(alpha = 0.5f)
			else
				textColor.copy(alpha = 0.7f),
			maxLines = 1,
			modifier = Modifier.padding(start = Dimensions.Spacing.tiny)
		)
	}
}

@Composable
@Preview(showBackground = true)
fun PreviewDayCell() {
	MyCalendarTheme {
		DayCell(
			LocalDate.now(), isSelected = false, isCurrentMonth = true, hasTodo = true, exampleScheduleItemList
		) {}
	}
}