package cn.szu.blankxiao.mycalendar.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.tooling.preview.Preview
import cn.szu.blankxiao.mycalendar.data.calendar.CalendarMode
import cn.szu.blankxiao.mycalendar.data.calendar.DayPosition
import cn.szu.blankxiao.mycalendar.data.calendar.rememberCustomCalendarState
import cn.szu.blankxiao.mycalendar.data.schedule.ScheduleItemData
import cn.szu.blankxiao.mycalendar.reminder.ReminderManager
import cn.szu.blankxiao.mycalendar.ui.calendar.common.DayCell
import cn.szu.blankxiao.mycalendar.ui.calendar.specific.AnimatableCustomCalendar
import cn.szu.blankxiao.mycalendar.ui.schedule.AddScheduleDialog
import cn.szu.blankxiao.mycalendar.ui.schedule.EditScheduleDialog
import cn.szu.blankxiao.mycalendar.ui.schedule.ScheduleList
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme
import cn.szu.blankxiao.mycalendar.ui.theme.Typography
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import cn.szu.blankxiao.mycalendar.viewmodel.ScheduleViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * @author BlankXiao
 * @description 主页面 - 日历与日程
 * @date 2025-12-07 21:04
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
	val context = androidx.compose.ui.platform.LocalContext.current

	// 日历范围配置
	val monthDelta = 12L
	val currentMonth = remember { YearMonth.now() }
	val startDate = currentMonth.minusMonths(monthDelta).atDay(1)
	val endDate = currentMonth.plusMonths(monthDelta).atEndOfMonth()

	// 日历状态
	val calendarState = rememberCustomCalendarState(
		startDate = startDate,
		endDate = endDate,
		firstDayOfWeek = DayOfWeek.MONDAY,
		initialVisibleDate = LocalDate.now()
	)

	// 从ViewModel获取所有日程数据（响应式）
	val allSchedules by viewModel.allSchedules.collectAsState()

	// 获取选中日期的日程
	val selectedDateSchedules = remember(calendarState.selectedDate, allSchedules) {
		allSchedules.filter { it.date == calendarState.selectedDate }
	}

	// 日期格式化
	val dateFormatter = remember {
		DateTimeFormatter.ofPattern("M月d日 EEEE", Locale.CHINA)
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
				// 左侧占位（保持标题居中）
				Box(modifier = Modifier.width(Dimensions.Spacing.huge))
				
				// 中间的标题
				Column(
					modifier = Modifier.weight(1f),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					// 当前选中日期
					Text(
						text = calendarState.selectedDate.format(dateFormatter),
						style = Typography.titleLarge,
						fontWeight = FontWeight.Bold,
						color = customColors.calendarNormalText
					)
					// 当前模式
					Text(
						text = "当前模式: ${if (calendarState.calendarMode == CalendarMode.MONTH) "月视图" else "周视图"}",
						style = Typography.bodySmall,
						color = customColors.calendarOtherMonthText
					)
				}

				// 右侧的设置按钮
				Box {
					IconButton(onClick = { showMenu = true }) {
						Icon(
							imageVector = Icons.Default.MoreVert,
							contentDescription = "更多选项",
							tint = customColors.calendarNormalText
						)
					}
					
					// 下拉菜单
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
					}
				}
			}

			// 日历组件
			AnimatableCustomCalendar(
				state = calendarState,
				modifier = Modifier.fillMaxWidth()
			) { day ->
				val isSelected = day.date == calendarState.selectedDate
				val isCurrentMonth = day.position == DayPosition.MonthDate

				// 检查该日期是否有日程
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

			// 日程列表
			ScheduleList(
				scheduleDataList = selectedDateSchedules,
				modifier = Modifier
					.fillMaxWidth()
					.weight(1f),
				onItemToggle = { viewModel.toggleScheduleStatus(it) },
				onItemDelete = { viewModel.deleteSchedule(it) },
				onItemLongPress = { item ->
					editingSchedule = item
					showEditDialog = true
				}
			)
		}

		// 浮动添加按钮
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

	// 对话框部分
	// 添加日程对话框
	if (showAddDialog) {
		AddScheduleDialog(
			selectedDate = calendarState.selectedDate,
			onDismiss = { showAddDialog = false },
			onConfirm = { title, date, description, reminderEnabled, reminderDateTime ->
				val scheduleData = ScheduleItemData(
					title = title,
					date = date,
					desc = description,
					isChecked = false,
					reminderEnabled = reminderEnabled,
					reminderTime = reminderDateTime
				)

				// 添加日程
				coroutineScope.launch {
					val scheduleId = viewModel.addScheduleAndGetId(scheduleData)

					// 如果启用提醒，设置提醒
					if (reminderEnabled && reminderDateTime != null) {
						ReminderManager.setReminder(
							context,
							scheduleData.copy(id = scheduleId)
						)
					}
				}
			}
		)
	}

	// 编辑日程对话框
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
					desc = description,
					reminderEnabled = reminderEnabled,
					reminderTime = reminderDateTime
				)
				viewModel.updateSchedule(updatedSchedule)

				// 更新提醒设置
				coroutineScope.launch {
					if (reminderEnabled && reminderDateTime != null) {
						ReminderManager.setReminder(context, updatedSchedule)
					} else {
						// 取消提醒
						ReminderManager.cancelReminder(context, updatedSchedule.id)
					}
				}

				showEditDialog = false
				editingSchedule = null
			},
			onDelete = {
				viewModel.deleteSchedule(editingSchedule!!)
				// 取消提醒
				ReminderManager.cancelReminder(context, editingSchedule!!.id)
				showEditDialog = false
				editingSchedule = null
			}
		)
	}
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun PreviewMainScreen() {
	MyCalendarTheme {
		MainScreen()
	}
}
