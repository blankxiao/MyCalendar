package cn.szu.blankxiao.mycalendar.ui.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.szu.blankxiao.mycalendar.data.todo.TodoItemData
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme
import cn.szu.blankxiao.mycalendar.ui.theme.Typography
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import java.time.LocalDate

/**
 * @author BlankXiao
 * @description TodoList - 待办事项列表
 * @date 2025-11-08 15:52
 */

private const val TAG = "TodoList"

/**
 * TodoList 组件
 * 适用于 BottomSheet 中显示
 * 
 * @param todoDataList 待办事项数据列表
 * @param title 列表标题（可选）
 * @param onItemToggle 任务完成状态切换回调
 * @param modifier 修饰符
 */
@Composable
fun TodoList(
	todoDataList: List<TodoItemData>,
	modifier: Modifier = Modifier,
	title: String = "今日待办",
	onItemToggle: (TodoItemData) -> Unit = {}
) {
	val customColors = MaterialTheme.customColors

	Column(
		modifier = modifier
			.fillMaxWidth()
			.background(customColors.todoListBackground)
	) {
		// 标题区域
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(
					horizontal = Dimensions.Padding.large,
					vertical = Dimensions.Padding.medium
				)
		) {
			Text(
				text = title,
				style = Typography.titleLarge,
				fontWeight = FontWeight.Bold,
				color = customColors.todoListTitleText
			)

			// 任务统计
			val completedCount = todoDataList.count { it.isChecked }
			val totalCount = todoDataList.size

			if (totalCount > 0) {
				Text(
					text = "已完成 $completedCount / $totalCount",
					style = Typography.bodyMedium,
					color = customColors.todoListEmptyText,
					modifier = Modifier.padding(top = Dimensions.Padding.tiny)
				)
			}
		}

		HorizontalDivider(
			color = customColors.calendarDivider,
			thickness = 1.dp
		)

		// 内容区域
		if (todoDataList.isEmpty()) {
			// 空状态
			EmptyTodoState()
		} else {
			// 任务列表（支持滚动）
			LazyColumn(
				modifier = Modifier
					.fillMaxWidth()
					.weight(1f, fill = false), // 不强制填充剩余空间
				verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.extraSmall)
			) {
				items(todoDataList, key = { it.hashCode() }) { todoItem ->
					TodoItem(
						itemData = todoItem,
						onChecked = {
							onItemToggle(todoItem)
						}
					)
				}
			}
		}
	}
}

/**
 * 空状态组件
 */
@Composable
private fun EmptyTodoState(modifier: Modifier = Modifier) {
	val customColors = MaterialTheme.customColors

	Box(
		modifier = modifier
			.fillMaxWidth()
			.padding(vertical = 48.dp),
		contentAlignment = Alignment.Center
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.small)
		) {
			Icon(
				imageVector = Icons.Default.CheckCircle,
				contentDescription = "无待办事项",
				tint = customColors.todoListEmptyText,
				modifier = Modifier.size(64.dp)
			)

			Text(
				text = "暂无待办事项",
				style = Typography.bodyLarge,
				color = customColors.todoListEmptyText
			)

			Text(
				text = "享受轻松的一天 ✨",
				style = Typography.bodyMedium,
				color = customColors.todoListEmptyText
			)
		}
	}
}

@Composable
@Preview(showBackground = true)
fun PreviewTodoList() {
	val todoData1 = TodoItemData("完成项目文档", LocalDate.now(), "按时提交文档", false)
	val todoData2 = TodoItemData("团队会议", LocalDate.now(), "下午3点开会", false)
	val todoData3 = TodoItemData("代码 Review", LocalDate.now(), "审核 PR #123", true)
	val todoDataList = remember {
		mutableStateListOf(todoData1, todoData2, todoData3)
	}

	MyCalendarTheme {
		TodoList(
			todoDataList = todoDataList,
			title = "今日待办",
			onItemToggle = { item ->
				item.isChecked = !item.isChecked
			}
		)
	}
}

@Composable
@Preview(showBackground = true)
fun PreviewEmptyTodoList() {
	MyCalendarTheme {
		TodoList(
			todoDataList = emptyList(),
			title = "今日待办"
		)
	}
}
