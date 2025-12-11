package cn.szu.blankxiao.mycalendar.ui.schedule

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import cn.szu.blankxiao.mycalendar.data.schedule.ScheduleItemData
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme
import cn.szu.blankxiao.mycalendar.ui.theme.Typography
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import java.time.LocalDate

/**
 * @author BlankXiao
 * @description ScheduleList - 日程列表
 * @date 2025-11-08 15:52
 */

private const val TAG = "ScheduleList"

/**
 * 日程列表组件
 * 适用于 BottomSheet 中显示
 * 
 * @param scheduleDataList 日程数据列表
 * @param title 列表标题（可选）
 * @param onItemToggle 日程完成状态切换回调
 * @param onItemDelete 日程删除回调
 * @param onItemLongPress 日程长按回调（编辑）
 * @param modifier 修饰符
 */
@Composable
fun ScheduleList(
	scheduleDataList: List<ScheduleItemData>,
	modifier: Modifier = Modifier,
	title: String = "今日日程",
	onItemToggle: (ScheduleItemData) -> Unit = {},
	onItemDelete: ((ScheduleItemData) -> Unit)? = null,
	onItemLongPress: ((ScheduleItemData) -> Unit)? = null
) {
	val customColors = MaterialTheme.customColors

	Column(
		modifier = modifier
			.fillMaxWidth()
			.background(customColors.scheduleListBackground)
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
				color = customColors.scheduleListTitleText
			)

			// 日程统计
			val completedCount = scheduleDataList.count { it.isChecked }
			val totalCount = scheduleDataList.size

			if (totalCount > 0) {
				Text(
					text = "已完成 $completedCount / $totalCount",
					style = Typography.bodyMedium,
					color = customColors.scheduleListEmptyText,
					modifier = Modifier.padding(top = Dimensions.Padding.tiny)
				)
			}
		}

		HorizontalDivider(
			color = customColors.calendarDivider,
			thickness = 1.dp
		)

		// 内容区域
		if (scheduleDataList.isEmpty()) {
			// 空状态
			EmptyScheduleState()
		} else {
			// 日程列表（支持滚动 + 动画）
			LazyColumn(
				modifier = Modifier
					.fillMaxWidth()
					.weight(1f, fill = false), // 不强制填充剩余空间
				verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.extraSmall)
			) {
				items(
					items = scheduleDataList,
					key = { it.id }
				) { scheduleItem ->
					ScheduleItem(
						itemData = scheduleItem,
						modifier = Modifier.animateItem(
							fadeInSpec = spring(
								dampingRatio = Spring.DampingRatioMediumBouncy,
								stiffness = Spring.StiffnessLow
							),
							placementSpec = spring(
								dampingRatio = Spring.DampingRatioMediumBouncy,
								stiffness = Spring.StiffnessMedium
							),
							fadeOutSpec = spring(
								dampingRatio = Spring.DampingRatioMediumBouncy,
								stiffness = Spring.StiffnessLow
							)
						),
						onChecked = {
							onItemToggle(scheduleItem)
						},
						onDelete = if (onItemDelete != null) {
							{ onItemDelete(scheduleItem) }
						} else null,
						onLongPress = if (onItemLongPress != null) {
							{ onItemLongPress(scheduleItem) }
						} else null
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
private fun EmptyScheduleState(modifier: Modifier = Modifier) {
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
				contentDescription = "无日程安排",
				tint = customColors.scheduleListEmptyText,
				modifier = Modifier.size(64.dp)
			)

			Text(
				text = "暂无日程安排",
				style = Typography.bodyLarge,
				color = customColors.scheduleListEmptyText
			)

			Text(
				text = "享受轻松的一天 ✨",
				style = Typography.bodyMedium,
				color = customColors.scheduleListEmptyText
			)
		}
	}
}

@Composable
@Preview(showBackground = true)
fun PreviewScheduleList() {
	val schedule1 = ScheduleItemData("完成项目文档", LocalDate.now(), "按时提交文档", false)
	val schedule2 = ScheduleItemData("团队会议", LocalDate.now(), "下午3点开会", false)
	val schedule3 = ScheduleItemData("代码 Review", LocalDate.now(), "审核 PR #123", true)
	val scheduleList = remember {
		mutableStateListOf(schedule1, schedule2, schedule3)
	}

	MyCalendarTheme {
		ScheduleList(
			scheduleDataList = scheduleList,
			title = "今日日程",
			onItemToggle = { item ->
				item.isChecked = !item.isChecked
			}
		)
	}
}

@Composable
@Preview(showBackground = true)
fun PreviewEmptyScheduleList() {
	MyCalendarTheme {
		ScheduleList(
			scheduleDataList = emptyList(),
			title = "今日日程"
		)
	}
}
