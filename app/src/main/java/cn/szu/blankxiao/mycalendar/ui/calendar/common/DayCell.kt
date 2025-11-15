package cn.szu.blankxiao.mycalendar.ui.calendar.common

import android.util.Log
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.szu.blankxiao.mycalendar.data.TodoItemData
import cn.szu.blankxiao.mycalendar.data.exampleTodoItemList
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme
import cn.szu.blankxiao.mycalendar.ui.theme.Typography
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import cn.szu.blankxiao.mycalendar.utils.LunarUtil
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
	hasTodo: Boolean,
	todoDataList: List<TodoItemData>?,
	showTodoContent: Boolean = false,  // 是否显示 todo 详细内容（展开状态）
	onClick: () -> Unit,
) {
	val isToday = day == LocalDate.now()
	val customColors = MaterialTheme.customColors

	Log.d(TAG, "渲染 - day=$day, isSelected=$isSelected, isToday=$isToday")

	val textColor = when {
		isSelected -> customColors.calendarSelectedText
		isToday -> customColors.calendarTodayText
		!isCurrentMonth -> customColors.calendarOtherMonthText
		else -> customColors.calendarNormalText
	}

	val lunarText = remember(day) {
		LunarUtil.getLunarDayText(day)
	}

	// 外层 Column：包含圆形日期区域 + Todo 内容
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Top
	) {
		// 圆形日期区域
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
			// 待办指示点（绝对定位在顶部）
			if (hasTodo && !isSelected) {
				Box(
					modifier = Modifier
						.align(Alignment.TopCenter)
						.offset(y = Dimensions.Spacing.tiny)
						.size(Dimensions.Size.tiny)
						.clip(CircleShape)
						.background(customColors.calendarTodoDot)
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

				// 农历日期（小字）
				if (lunarText.isNotEmpty()) {
					Text(
						text = lunarText,
						style = Typography.labelSmall,
						color = textColor.copy(alpha = 0.6f)
					)
				}
			}
		}

		// TodoData 内容（在圆圈外部）
		if (showTodoContent && !todoDataList.isNullOrEmpty()) {
			Column(
				modifier = Modifier
					.padding(top = Dimensions.Padding.tiny)
					.padding(horizontal = Dimensions.Padding.tiny),
				verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.tiny),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				todoDataList.take(2).forEach { todoItem ->
					simpleTodoItem(todoItem, textColor)
				}
				
				// 如果还有更多 todo，显示提示
				if (todoDataList.size > 2) {
					Text(
						text = "📝 还有 ${todoDataList.size - 2} 项",
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
private fun simpleTodoItem(
	todoItem: TodoItemData,
	textColor: Color
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.Center,
		modifier = Modifier.fillMaxWidth()
	) {
		// 表情图标（根据完成状态）
		Text(
			text = if (todoItem.isChecked) "✅" else "📌",
			style = Typography.labelSmall,
			fontSize = 10.sp
		)

		// Todo 标题（限制长度）
		Text(
			text = todoItem.title.take(4),  // 最多显示 4 个字
			style = Typography.labelSmall,
			color = if (todoItem.isChecked)
				textColor.copy(alpha = 0.5f)
			else
				textColor.copy(alpha = 0.7f),
			maxLines = 1,
			modifier = Modifier.padding(start = 2.dp)
		)
	}
}

@Composable
@Preview(showBackground = true)
fun PreviewDayCell() {
	MyCalendarTheme {
		DayCell(
			LocalDate.now(), isSelected = false, isCurrentMonth = true, hasTodo = true, exampleTodoItemList
		) {}
	}
}