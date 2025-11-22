package cn.szu.blankxiao.mycalendar.ui.calendar.common

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

/**
 * @author BlankXiao
 * @description CustomCalendar - 基于 NCalendar 思路的双层动画实现
 * @date 2025-11-22 15:27
 */

private const val TAG = "CustomCalendar"

@Composable
fun AnimatableMonthCalendar(
	state: CalendarState,
	targetWeekIndex: Int, // 目标周的索引
	weekTransitionProgress: Float, // 0f = 月模式, 1f = 周模式
	dayContent: @Composable BoxScope.(CalendarDay) -> Unit,
) {
	val month = state.firstVisibleMonth
	val density = LocalDensity.current

	val weekHeightDp = 60f  // 单周高度
	val weekCount = month.weekDays.size  // 总周数
	val monthHeightDp = weekHeightDp * weekCount  // 月视图总高度
	
	// 计算目标周的顶部位置
	val targetWeekTopDp = targetWeekIndex * weekHeightDp

	// 计算容器高度
	val heightOffset = monthHeightDp - weekHeightDp
	val containerHeight = monthHeightDp - (heightOffset * weekTransitionProgress)

	// 计算内容偏移量
	val contentOffsetYDp = targetWeekTopDp * weekTransitionProgress
	
	// 转换为 px（layout 和 place 需要 px 单位）
	val contentOffsetYPx = with(density) { contentOffsetYDp.dp.toPx() }.toInt()
	val containerHeightPx = with(density) { containerHeight.dp.toPx() }.toInt()


	Box(
		modifier = Modifier
			.fillMaxWidth()
			.height(containerHeight.dp)
			.clipToBounds()
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.layout { measurable, constraints ->
					val placeable = measurable.measure(
						constraints.copy(
							minHeight = 0,
							maxHeight = Int.MAX_VALUE
						)
					)
					layout(placeable.width, containerHeightPx) {
						// 固定目标周：向上移动 contentOffsetYPx（px 单位）
						placeable.place(0, -contentOffsetYPx)
					}
				}
		) {
			month.weekDays.forEachIndexed { index, week ->

				Row(
					modifier = Modifier
						.fillMaxWidth()
						.height(weekHeightDp.dp)  // 明确设置高度
				) {
					week.forEach { day ->
						Box(modifier = Modifier.weight(1f)) {
							dayContent(day)
						}
					}
				}
			}
		}
	}
}

/**
 * 拖动句柄组件
 */
@Composable
fun DragHandle(modifier: Modifier = Modifier) {
	Box(
		modifier = modifier,
		contentAlignment = Alignment.Center
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth(0.2f)
				.height(4.dp)
				.clip(RoundedCornerShape(2.dp))
				.background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
		)
	}
}


@Composable
@Preview(showBackground = true)
fun PreviewAnimatableMonthCalendar() {
	// 组件相关状态
	val monthDelta = 100L
	// 当前月份
	val currentMonth = remember { YearMonth.now() }
	// 开始月份
	val startMonth = currentMonth.minusMonths(monthDelta)
	// 结束月份
	val endMonth = currentMonth.plusMonths(monthDelta)
	val state = rememberCalendarState(
		startMonth = startMonth,
		endMonth = endMonth,
		firstDayOfWeek = DayOfWeek.MONDAY,
		firstVisibleMonth = currentMonth
	)

	// 使用 mutableFloatStateOf 支持连续值变化
	var weekTransitionProgress by remember { mutableFloatStateOf(0f) }
	var selectedDate by remember { mutableStateOf(LocalDate.now()) }

	MyCalendarTheme {
		Column(
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.Center
			) {
				Button(onClick = {
					weekTransitionProgress = if (weekTransitionProgress == 0f) 1f else 0f
				}) {
					Text("转换")
				}
				Text("  进度: %.2f".format(weekTransitionProgress))
			}

			// 调试信息（可选）
			Text(
				text = "调试: 容器高度变化 | 内容向上平移",
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
			)

			Box(
				modifier = Modifier
					.fillMaxWidth()
					.draggable(
						orientation = Orientation.Vertical,
						state = rememberDraggableState { delta ->
							// delta 为负数表示向上滑动（切换到周视图）
							// delta 为正数表示向下滑动（切换到月视图）
							val sensitivity = 0.002f // 调整灵敏度
							weekTransitionProgress =
								(weekTransitionProgress - delta * sensitivity).coerceIn(0f, 1f)
						}
					)
			) {
				AnimatableMonthCalendar(
					state = state,
					targetWeekIndex = selectedDate.dayOfMonth / 7,
					weekTransitionProgress = weekTransitionProgress,
				) { day ->
					val isSelected = day.date == selectedDate

					// val currentDayTodo = date2TodoDataList[day.date]
					val currentDayTodo = null
					val showSpot = currentDayTodo != null && !isSelected
					val isCurrentMonth = day.position == DayPosition.MonthDate
					DayCell(
						day = day.date,
						isSelected = isSelected,
						isCurrentMonth = isCurrentMonth,
						hasTodo = showSpot,
						todoDataList = currentDayTodo,
						showTodoContent = false
					) {
						selectedDate = day.date
						// onDateSelected(day.date)
					}
				}
			}

			DragHandle()

			Spacer(modifier = Modifier
				.fillMaxSize()
				.background(Color.Black))
		}
	}
}


