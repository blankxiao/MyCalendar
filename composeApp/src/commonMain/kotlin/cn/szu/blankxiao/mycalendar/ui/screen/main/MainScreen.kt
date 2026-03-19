package cn.szu.blankxiao.mycalendar.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.zIndex
import cn.szu.blankxiao.mycalendar.model.calendar.CalendarMode
import cn.szu.blankxiao.mycalendar.model.calendar.DayPosition
import cn.szu.blankxiao.mycalendar.model.calendar.YearMonth
import cn.szu.blankxiao.mycalendar.model.calendar.rememberCustomCalendarState
import cn.szu.blankxiao.mycalendar.model.schedule.ScheduleItemData
import cn.szu.blankxiao.mycalendar.service.reminder.ReminderScheduler
import cn.szu.blankxiao.mycalendar.ui.component.calendar.AnimatableCustomCalendar
import cn.szu.blankxiao.mycalendar.ui.component.calendar.DayCell
import cn.szu.blankxiao.mycalendar.ui.component.dialog.AddScheduleDialog
import cn.szu.blankxiao.mycalendar.ui.component.dialog.EditScheduleDialog
import cn.szu.blankxiao.mycalendar.ui.component.schedule.ScheduleList
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme
import cn.szu.blankxiao.mycalendar.ui.theme.Typography
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import cn.szu.blankxiao.mycalendar.util.formatForDisplay
import cn.szu.blankxiao.mycalendar.viewmodel.ScheduleViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * 主页面 - 日历与日程
 * 含 CustomCalendar（月/周视图滑动）、ScheduleList
 */
@Composable
fun MainScreen(
	modifier: Modifier = Modifier,
	viewModel: ScheduleViewModel = koinViewModel(),
	onNavigateToSettings: () -> Unit = {},
	onNavigateToDayView: (LocalDate) -> Unit = {}
) {
	val coroutineScope = rememberCoroutineScope()
	val customColors = MaterialTheme.customColors
	val reminderScheduler = koinInject<ReminderScheduler>()

	// 日历范围配置
	val monthDelta = 12L
	val currentMonth = remember {
		YearMonth.from(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
	}
	val startDate = remember { currentMonth.minusMonths(monthDelta).atDay(1) }
	val endDate = remember { currentMonth.plusMonths(monthDelta).atEndOfMonth() }

	// 日历状态
	val calendarState = rememberCustomCalendarState(
		startDate = startDate,
		endDate = endDate,
		firstDayOfWeek = DayOfWeek.MONDAY,
		initialVisibleDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
	)

	// 从ViewModel获取所有日程数据（响应式）
	val allSchedules by viewModel.allSchedules.collectAsState()

	// 获取选中日期的日程
	val selectedDateSchedules = remember(calendarState.selectedDate, allSchedules) {
		allSchedules.filter { it.date == calendarState.selectedDate }
	}

	// 对话框状态
	var showAddDialog by remember { mutableStateOf(false) }
	var showEditDialog by remember { mutableStateOf(false) }
	var editingSchedule by remember { mutableStateOf<ScheduleItemData?>(null) }
	
	// 菜单状态
	var showMenu by remember { mutableStateOf(false) }

	// Snackbar状态
	val snackbarHostState = remember { SnackbarHostState() }

	Box(
		modifier = modifier
			.fillMaxSize()
			.background(customColors.calendarBackground)
	) {
		// Snackbar Host
		Box(
			modifier = Modifier
				.align(Alignment.BottomCenter)
				.fillMaxWidth()
		) {
			SnackbarHost(hostState = snackbarHostState)
		}
		Column(
			modifier = Modifier.fillMaxSize()
		) {
			// 日历区域容器 - 添加阴影和圆角以区分下方列表
			Surface(
				modifier = Modifier
					.fillMaxWidth()
					.zIndex(1f),
				shape = RoundedCornerShape(
					bottomStart = Dimensions.CornerRadius.large,
					bottomEnd = Dimensions.CornerRadius.large
				),
				shadowElevation = Dimensions.Elevation.medium,
				color = customColors.calendarBackground
			) {
				Column(
					modifier = Modifier.fillMaxWidth()
				) {
					// 顶部标题区域（带设置按钮）
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(
								horizontal = Dimensions.Padding.large,
								vertical = Dimensions.Padding.medium
							),
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.SpaceBetween
					) {
						Box(modifier = Modifier.width(Dimensions.Spacing.huge))
						
						Column(
							modifier = Modifier.weight(1f),
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							Text(
								text = calendarState.selectedDate.formatForDisplay(),
								style = Typography.titleLarge,
								fontWeight = FontWeight.Bold,
								color = customColors.calendarNormalText
							)
							Text(
								text = "当前模式: ${if (calendarState.calendarMode == CalendarMode.MONTH) "月视图" else "周视图"}",
								style = Typography.bodySmall,
								color = customColors.calendarOtherMonthText
							)
						}

						Box {
							IconButton(onClick = { showMenu = true }) {
								Icon(
									imageVector = Icons.Default.MoreVert,
									contentDescription = "更多选项",
									tint = customColors.calendarNormalText
								)
							}
							
							DropdownMenu(
								expanded = showMenu,
								onDismissRequest = { showMenu = false },
								containerColor = customColors.surface
							) {
								DropdownMenuItem(
									text = { 
										Text(
											"设置",
											color = customColors.textPrimary
										) 
									},
									onClick = {
										showMenu = false
										onNavigateToSettings()
									},
									colors = MenuDefaults.itemColors(
										textColor = customColors.textPrimary,
										leadingIconColor = customColors.textPrimary
									)
								)
								DropdownMenuItem(
									text = { 
										Text(
											"日视图",
											color = customColors.textPrimary
										) 
									},
									onClick = {
										showMenu = false
										onNavigateToDayView(calendarState.selectedDate)
									},
									colors = MenuDefaults.itemColors(
										textColor = customColors.textPrimary,
										leadingIconColor = customColors.textPrimary
									)
								)
							}
						}
					}

					// 日历组件（月/周视图滑动）
					AnimatableCustomCalendar(
						state = calendarState,
						modifier = Modifier.fillMaxWidth()
							.padding(bottom = Dimensions.Padding.small)
					) { day ->
						val isSelected = day.date == calendarState.selectedDate
						val isCurrentMonth = day.position == DayPosition.MonthDate

						val daySchedules = allSchedules.filter { it.date == day.date }
						val hasSchedule = daySchedules.isNotEmpty() && !isSelected

						DayCell(
							day = day.date,
							isSelected = isSelected,
							isCurrentMonth = isCurrentMonth,
							hasTodo = hasSchedule,
							scheduleDataList = daySchedules.ifEmpty { null },
							modifier = Modifier.weight(1f),
							showScheduleContent = false,
							onClick = {
								coroutineScope.launch {
									calendarState.scrollToDate(day.date)
								}
							},
							onDoubleClick = {
								onNavigateToDayView(day.date)
							}
						)
					}
				}
			}

			// 日程列表
			ScheduleList(
				scheduleDataList = selectedDateSchedules,
				modifier = Modifier
					.fillMaxWidth()
					.weight(1f)
					.background(customColors.scheduleListBackground),
				onItemToggle = { viewModel.toggleScheduleStatus(it) },
				onItemDelete = { viewModel.deleteSchedule(it) },
				onItemLongPress = { item ->
					editingSchedule = item
					showEditDialog = true
				}
			)
		}

		FloatingActionButton(
			onClick = { showAddDialog = true },
			modifier = Modifier
				.align(Alignment.BottomEnd)
				.padding(Dimensions.Padding.large),
			containerColor = customColors.buttonPrimaryBackground,
			contentColor = customColors.buttonPrimaryText
		) {
			Icon(
				imageVector = Icons.Default.Add,
				contentDescription = "添加日程"
			)
		}
	}

	if (showAddDialog) {
		AddScheduleDialog(
			selectedDate = calendarState.selectedDate,
			onDismiss = { showAddDialog = false },
			onConfirm = { title, date, description, reminderEnabled, reminderDateTime ->
				val scheduleData = ScheduleItemData(
					title = title,
					date = date,
					description = description,
					isChecked = false,
					reminderEnabled = reminderEnabled,
					reminderTime = reminderDateTime
				)

				coroutineScope.launch {
					val scheduleId = viewModel.addScheduleAndGetId(scheduleData)

					if (reminderEnabled && reminderDateTime != null) {
						reminderScheduler.setReminder(scheduleData.copy(id = scheduleId))
					}
				}
				showAddDialog = false
			}
		)
	}

	if (showEditDialog && editingSchedule != null) {
		EditScheduleDialog(
			scheduleData = editingSchedule!!,
			onDismiss = {
				showEditDialog = false
				editingSchedule = null
			},
			onConfirm = { id, title, date, description, reminderEnabled, reminderDateTime ->
				val updatedSchedule = editingSchedule!!.copy(
					id = id,
					title = title,
					date = date,
					description = description,
					reminderEnabled = reminderEnabled,
					reminderTime = reminderDateTime
				)
				viewModel.updateSchedule(updatedSchedule)

				coroutineScope.launch {
					if (reminderEnabled && reminderDateTime != null) {
						reminderScheduler.setReminder(updatedSchedule)
					} else {
						reminderScheduler.cancelReminder(updatedSchedule.id)
					}
				}

				showEditDialog = false
				editingSchedule = null
			},
			onDelete = {
				viewModel.deleteSchedule(editingSchedule!!)
				reminderScheduler.cancelReminder(editingSchedule!!.id)
				showEditDialog = false
				editingSchedule = null
			}
		)
	}
}

