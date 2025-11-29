package cn.szu.blankxiao.mycalendar.ui.calendar.common

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.szu.blankxiao.mycalendar.data.calendar.CalendarMode
import cn.szu.blankxiao.mycalendar.data.calendar.CustomCalendarDay
import cn.szu.blankxiao.mycalendar.data.calendar.CustomCalendarMonth
import cn.szu.blankxiao.mycalendar.data.calendar.CustomCalendarState
import cn.szu.blankxiao.mycalendar.data.calendar.DayPosition
import cn.szu.blankxiao.mycalendar.data.calendar.ModeChangeDragThreshold
import cn.szu.blankxiao.mycalendar.data.calendar.rememberCustomCalendarState
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme
import kotlinx.coroutines.launch
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
fun AnimatableCustomCalendar(
	state: CustomCalendarState,
	modifier: Modifier = Modifier,
	dayContent: @Composable RowScope.(CustomCalendarDay) -> Unit,
) {
	val targetWeekIndex = state.targetWeekIndex
	// 用于转换dp和px
	val density = LocalDensity.current
	val weekTransitionProgress = state.transitionProgress

	// 单周的高度
	var weekHeightDp by remember { mutableFloatStateOf(60f) }
	val weekCount = state.weekCountInMonth
	val monthHeightDp = weekHeightDp * weekCount

	// 计算目标周的顶部位置 用于状态转换
	val targetWeekTopDp = targetWeekIndex * weekHeightDp

	// 计算容器高度
	val heightOffset = monthHeightDp - weekHeightDp
	val containerHeight = monthHeightDp - (heightOffset * weekTransitionProgress)

	// 计算内容偏移量
	val contentOffsetYDp = targetWeekTopDp * weekTransitionProgress

	// 转换为 px
	val contentOffsetYPx = with(density) { contentOffsetYDp.dp.toPx() }.toInt()
	val containerHeightPx = with(density) { containerHeight.dp.toPx() }.toInt()

	Column(
		modifier = modifier
			.padding(horizontal = Dimensions.Padding.small)
	) {
		DaysOfWeekTitle()
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(containerHeight.dp)
				.clipToBounds()
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
			HorizontalPager(
				state = state.pagerState,
				modifier = Modifier
					.fillMaxWidth()
			) { pageIndex ->
				val monthData = state.getMonthByIndex(pageIndex)
				val isCurrentPage = pageIndex == state.pagerState.currentPage

				MonthGrid(
					monthData = monthData,
					onWeekHeightMeasured = if (isCurrentPage) { height ->
						if (weekHeightDp != height) {
							weekHeightDp = height
						}
					} else null,
					dayContent = dayContent
				)
			}
		}

		DragHandle(
			state = state,
			modifier = Modifier
		)
	}
}


/**
 * 月份网格组件
 * 负责渲染单个月份的日历网格
 */
@Composable
fun MonthGrid(
	monthData: CustomCalendarMonth,
	modifier: Modifier = Modifier,
	onWeekHeightMeasured: ((Float) -> Unit)? = null,
	dayContent: @Composable RowScope.(CustomCalendarDay) -> Unit,
) {
	val density = LocalDensity.current

	Column(
		modifier = modifier
			.fillMaxWidth()
	) {
		monthData.weeks.forEachIndexed { weekIndex, week ->
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.wrapContentHeight()
					.then(
						// 只在第一周测量高度
						if (weekIndex == 0 && onWeekHeightMeasured != null) {
							Modifier.onSizeChanged { size ->
								if (size.height > 0) {
									val heightDp = with(density) { size.height.toDp().value }
									onWeekHeightMeasured(heightDp)
								}
							}
						} else {
							Modifier
						}
					)
			) {
				week.forEach { day ->
					dayContent(day)
				}
			}
		}
	}
}


/**
 * 拖动句柄组件
 */
@Composable
fun DragHandle(
	state: CustomCalendarState,
	modifier: Modifier = Modifier
) {
	val density = LocalDensity.current
	val coroutineScope = rememberCoroutineScope()

	Box(
		modifier = modifier
			.fillMaxWidth()
			.padding(vertical = Dimensions.Padding.small)
			.draggable(
				orientation = Orientation.Vertical,
				state = rememberDraggableState { delta ->
					// delta 为负数表示向上滑动（切换到周视图）
					// delta 为正数表示向下滑动（切换到月视图）

					// 拖动 200dp 完成完整过渡
					val dragDistancePx = with(density) { ModeChangeDragThreshold.dp.toPx() }
					val progressDelta = -delta / dragDistancePx

					val newProgress = (state.transitionProgress + progressDelta).coerceIn(0f, 1f)
					coroutineScope.launch {
						state.updateTransitionProgress(newProgress)
					}
				},
				onDragStopped = { velocity ->
					coroutineScope.launch {
						state.finishDragAndSnap(velocity)
					}
				}
			),
		contentAlignment = Alignment.Center
	) {
		// 拖动手柄
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier.padding(vertical = Dimensions.Padding.small)
		) {
			Box(
				modifier = Modifier
					.fillMaxWidth(0.15f)
					.height(Dimensions.Size.tiny)
					.clip(RoundedCornerShape(Dimensions.CornerRadius.small))
					.background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
			)
		}
	}
}


@Composable
@Preview(showBackground = true)
fun PreviewAnimatableMonthCalendar() {
	// 组件相关状态
	val monthDelta = 10L
	// 当前月份
	val currentMonth = remember { YearMonth.now() }
	// 开始日期
	val startDate = currentMonth.minusMonths(monthDelta).atDay(1)
	// 结束日期
	val endDate = currentMonth.plusMonths(monthDelta).atEndOfMonth()
	val state = rememberCustomCalendarState(
		startDate = startDate,
		endDate = endDate,
		firstDayOfWeek = DayOfWeek.MONDAY,
		initialVisibleDate = LocalDate.now()
	)

	val coroutineScope = rememberCoroutineScope()

	MyCalendarTheme {
		Column(
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					text = state.displayText, // 使用新的格式化文本
					style = MaterialTheme.typography.titleMedium
				)
				Text("模式: ${if (state.calendarMode == CalendarMode.MONTH) "月" else "周"}")

				// 测试按钮：切换模式
				Button(onClick = {
					coroutineScope.launch {
						state.toggleMode(animate = true)
					}
				}) {
					Text("切换模式")
				}
			}

			AnimatableCustomCalendar(
				state = state,
				modifier = Modifier.fillMaxWidth()
			) { day ->
				val isSelected = day.date == state.selectedDate
				val currentDayTodo: List<Nothing>? = null
				val showSpot = currentDayTodo != null && !isSelected
				val isCurrentMonth = day.position == DayPosition.MonthDate

				DayCell(
					day = day.date,
					isSelected = isSelected,
					isCurrentMonth = isCurrentMonth,
					hasTodo = showSpot,
					todoDataList = currentDayTodo,
					modifier = Modifier.weight(1f),
					showTodoContent = false
				) {
					coroutineScope.launch {
						state.selectDate(day.date, scrollToDate = true)
					}
				}
			}

			Spacer(
				modifier = Modifier
					.fillMaxSize()
					.background(Color.Black)
			)
		}
	}
}


