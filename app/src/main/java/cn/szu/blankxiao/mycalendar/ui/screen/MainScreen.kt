package cn.szu.blankxiao.mycalendar.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import cn.szu.blankxiao.mycalendar.data.TodoItemData
import cn.szu.blankxiao.mycalendar.ui.calendar.specific.MonthViewCalendar
import cn.szu.blankxiao.mycalendar.ui.calendar.specific.WeekViewCalendar
import cn.szu.blankxiao.mycalendar.ui.layout.BottomSheetState
import cn.szu.blankxiao.mycalendar.ui.layout.ThreeStateBottomSheet
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme
import cn.szu.blankxiao.mycalendar.ui.todo.TodoList
import java.time.LocalDate

/**
 * @author BlankXiao
 * @description MainScreen
 * @date 2025-11-13 19:54
 */

@Composable
fun MainScreen(
	currentSheetState: BottomSheetState = BottomSheetState.HALF,
	onStateChange: (BottomSheetState) -> Unit,
	onDateSelected: (LocalDate) -> Unit,
	todoDataList: List<TodoItemData>,
	date2TodoDataList: Map<LocalDate, List<TodoItemData>>,  // 新增：日期到 todo 列表的映射
	selectedDate: LocalDate,
) {
	ThreeStateBottomSheet(
		currentState = currentSheetState,
		onStateChange = onStateChange,
		mainContent = { state ->
			when (state) {
				// COLLAPSED 状态：月视图 + 显示 todo 详情
				BottomSheetState.COLLAPSED -> MonthViewCalendar(
					selectedDate = selectedDate,
					onDateSelected = onDateSelected,
					date2TodoDataList = date2TodoDataList,
					showTodoContent = true
				)

				// HALF 状态：月视图，不显示 todo 详情
				BottomSheetState.HALF -> MonthViewCalendar(
					selectedDate = selectedDate,
					onDateSelected = onDateSelected,
					date2TodoDataList = date2TodoDataList,
					showTodoContent = false
				)

				// EXPANDED 状态：周视图
				BottomSheetState.EXPANDED -> WeekViewCalendar(
					selectedDate = selectedDate,
					onDateSelected = onDateSelected
				)
			}
		},
		sheetContent = { state ->
			TodoList(todoDataList = todoDataList)
		}
	)
}


@Composable
@Preview
fun PreviewMainScreen() {
	MyCalendarTheme {
		var currentSheetState by remember { mutableStateOf(BottomSheetState.HALF) }
		var selectedDate by remember { mutableStateOf(LocalDate.now()) }
		var todoDataList = listOf<TodoItemData>()
		MainScreen(
			currentSheetState = currentSheetState,
			selectedDate = selectedDate,
			onStateChange = { currentSheetState = it },
			onDateSelected = { selectedDate = it },
			todoDataList = todoDataList,
			date2TodoDataList = mapOf()
		)

	}
}


