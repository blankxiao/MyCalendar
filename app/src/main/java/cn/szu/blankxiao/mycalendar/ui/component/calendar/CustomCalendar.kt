package cn.szu.blankxiao.mycalendar.ui.component.calendar

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import cn.szu.blankxiao.mycalendar.model.calendar.CalendarMode
import cn.szu.blankxiao.mycalendar.model.calendar.CustomCalendarData
import cn.szu.blankxiao.mycalendar.model.calendar.CustomCalendarDay
import cn.szu.blankxiao.mycalendar.model.calendar.CustomCalendarState
import cn.szu.blankxiao.mycalendar.model.calendar.DayPosition
import cn.szu.blankxiao.mycalendar.model.calendar.ModeChangeDragThreshold
import cn.szu.blankxiao.mycalendar.model.calendar.rememberCustomCalendarState
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

/**
 * @author BlankXiao
 * @description CustomCalendar实现
 * @date 2025-11-22 15:27
 */

private const val TAG = "CustomCalendar"

@Composable
fun AnimatableCustomCalendar(
	state: CustomCalendarState,
	modifier: Modifier = Modifier,
	dayContent: @Composable RowScope.(CustomCalendarDay) -> Unit,
) {
	// 监听 Pager 页面变化，更新 selectedDate
	LaunchedEffect(state.pagerState) {
		snapshotFlow { state.pagerState.currentPage }
			.collect { newPage ->
				state.onPageChanged(newPage)
			}
	}

	Column(
		modifier = modifier
			.padding(horizontal = Dimensions.Padding.small)
	) {
		DaysOfWeekTitle()
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.wrapContentHeight()
				.clipToBounds()
		) {

			HorizontalPager(
				state = state.pagerState,
				modifier = Modifier
					.fillMaxWidth()
			) { pageIndex ->
				val calendarData = state.getDataForPage(pageIndex)

				key(calendarData.yearMonth) {
					MonthGrid(
						monthData = calendarData,
						weekTransitionProgress = state.transitionProgress,
						selectedDate = state.selectedDate,
						dayContent = dayContent
					)
				}
			}
		}

		DragHandle(
			state = state,
			modifier = Modifier
		)
	}
}


/** 固定的最大周数 */
private const val FIXED_WEEK_COUNT = 6

/**
 * 月份网格组件
 * 负责渲染单个月份的日历网格
 * 使用固定高度（6周），通过动态间距适配不同周数的月份
 */
@Composable
fun MonthGrid(
	monthData: CustomCalendarData,
	selectedDate: LocalDate,
	weekTransitionProgress: Float,
	modifier: Modifier = Modifier,
	dayContent: @Composable RowScope.(CustomCalendarDay) -> Unit,
) {
	val density = LocalDensity.current
	// 单周的高度
	var weekHeightDp by remember { mutableFloatStateOf(60f) }
	val actualWeekCount = monthData.weekCount

	// 固定月份高度（始终为6周高度）
	val fixedMonthHeightDp = weekHeightDp * FIXED_WEEK_COUNT

	// 计算周间距 将多余空间平均分配到周之间
	val extraSpace = fixedMonthHeightDp - (weekHeightDp * actualWeekCount)
	val gapCount = actualWeekCount - 1
	val weekGapDp = if (gapCount > 0) extraSpace / gapCount else 0f

	// 每周的有效高度
	val effectiveWeekHeightDp = weekHeightDp + weekGapDp

	// 计算容器高度和偏移量
	val targetWeekIndex = monthData.calDateIndexInWeeks(selectedDate)
	// 目标周的顶部位置 用于后续视图切换
	val targetWeekTopDp = targetWeekIndex * effectiveWeekHeightDp

	// 根据 transitionProgress 渐变
	val heightOffset = fixedMonthHeightDp - weekHeightDp
	val containerHeight = fixedMonthHeightDp - (heightOffset * weekTransitionProgress)

	// 计算内容偏移量
	val contentOffsetYDp = targetWeekTopDp * weekTransitionProgress

	// 转换为 px
	val contentOffsetYPx = with(density) { contentOffsetYDp.dp.toPx() }.toInt()
	val containerHeightPx = with(density) { containerHeight.dp.toPx() }.toInt()

	Column(
		modifier = modifier
			.fillMaxWidth()
			// 避免挤压限制
			.layout { measurable, constraints ->
				val placeable = measurable.measure(
					constraints.copy(
						minHeight = 0,
						maxHeight = Int.MAX_VALUE
					)
				)
				layout(placeable.width, containerHeightPx) {
					placeable.place(0, -contentOffsetYPx)
				}
			}
	) {
		monthData.weeks.forEachIndexed { weekIndex, week ->
			key(weekIndex) {
				// 计算底部间距（最后一周不加）
				val bottomPadding = if (weekIndex < actualWeekCount - 1) weekGapDp.dp else 0.dp

				Row(
					modifier = Modifier
						.fillMaxWidth()
						.wrapContentHeight()
						.padding(bottom = bottomPadding)
						.then(
							// 只在第一周测量高度
							if (weekIndex == 0) {
								Modifier.onSizeChanged { size ->
									if (size.height > 0) {
										weekHeightDp = with(density) { size.height.toDp().value }
									}
								}
							} else {
								Modifier
							}
						)
				) {
					week.days.forEach { day ->
						key(day.date) {
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
					// delta 为负数表示向上滑动
					// delta 为正数表示向下滑动

					val dragDistancePx = with(density) { ModeChangeDragThreshold.dp.toPx() }
					val progressDelta = -delta / dragDistancePx

					// 更新state的transitionProgress
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
		val customColors = MaterialTheme.customColors
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier.padding(vertical = Dimensions.Padding.small)
		) {
			Box(
				modifier = Modifier
					.fillMaxWidth(0.15f)
					.height(Dimensions.Size.tiny)
					.clip(RoundedCornerShape(Dimensions.CornerRadius.small))
					.background(customColors.calendarDragHandle)
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
				Text("模式: ${if (state.calendarMode == CalendarMode.MONTH) "月" else "周"}")
			}

			AnimatableCustomCalendar(
				state = state,
				modifier = Modifier.fillMaxWidth()
			) { day ->
				val isSelected = day.date == state.selectedDate
				val currentDaySchedule: List<Nothing>? = null
				val showSpot = currentDaySchedule != null && !isSelected
				val isCurrentMonth = day.position == DayPosition.MonthDate

				DayCell(
					day = day.date,
					isSelected = isSelected,
					isCurrentMonth = isCurrentMonth,
					hasTodo = showSpot,
					scheduleDataList = currentDaySchedule,
					modifier = Modifier.weight(1f),
					showScheduleContent = false,
					onClick = {
						coroutineScope.launch {
							state.scrollToDate(day.date)
						}
					}
				)
			}

			Spacer(
				modifier = Modifier
					.fillMaxSize()
					.background(Color.Black)
			)
		}
	}
}


