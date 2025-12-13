package cn.szu.blankxiao.mycalendar.ui.schedule

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import cn.szu.blankxiao.mycalendar.data.schedule.ScheduleItemData
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme
import cn.szu.blankxiao.mycalendar.ui.theme.Typography
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import kotlinx.coroutines.delay
import java.time.LocalDate

/**
 * @author BlankXiao
 * @description ScheduleItem - 日程项组件（支持滑动删除）
 * @date 2025-11-03 21:06
 * 
 * 手势操作：
 * - 左滑 → 删除
 * - 点击复选框 → 标记完成/取消完成
 */

private const val TAG = "ScheduleItem"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleItem(
	itemData: ScheduleItemData,
	modifier: Modifier = Modifier,
	onDelete: (() -> Unit)? = null,
	onChecked: () -> Unit,
	onLongPress: (() -> Unit)? = null,
	onReminderTest: (() -> Unit)? = null,
) {

	// 控制项是否显示（用于删除动画）
	var isVisible by remember { mutableStateOf(true) }
	
	// 滑动状态（只支持左滑删除）
	val swipeState = rememberSwipeToDismissBoxState(
		confirmValueChange = { dismissValue ->
			when (dismissValue) {
				SwipeToDismissBoxValue.EndToStart -> {
					// 左滑 → 删除
					if (onDelete != null) {
						isVisible = false
						true // 允许dismiss
					} else {
						false
					}
				}
				else -> false // 不响应右滑
			}
		},
		positionalThreshold = { it * 0.5f } // 滑动阈值设为50%
	)
	
	// 删除动画完成后触发删除
	LaunchedEffect(isVisible) {
		if (!isVisible && onDelete != null) {
			delay(300) // 等待动画完成
			onDelete()
		}
	}
	
	AnimatedVisibility(
		visible = isVisible,
		exit = shrinkVertically(
			animationSpec = tween(300),
			shrinkTowards = Alignment.Top
		) + fadeOut(animationSpec = tween(300))
	) {
		SwipeToDismissBox(
			state = swipeState,
			modifier = modifier
				.fillMaxWidth()
				.padding(horizontal = Dimensions.Padding.medium, vertical = Dimensions.Padding.tiny),
			enableDismissFromStartToEnd = false, // 禁用右滑
			backgroundContent = {
				// 滑动背景（只显示删除）
				SwipeBackground(
					swipeDirection = swipeState.dismissDirection
				)
			}
		) {
			// 日程卡片内容
			ScheduleItemContent(
				itemData = itemData,
				onChecked = onChecked,
				onLongPress = onLongPress,
				onReminderTest = onReminderTest
			)
		}
	}
}

/**
 * 滑动背景 - 显示删除提示
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeBackground(
	swipeDirection: SwipeToDismissBoxValue
) {
	val customColors = MaterialTheme.customColors
	
	val backgroundColor = when (swipeDirection) {
		SwipeToDismissBoxValue.EndToStart -> customColors.scheduleSwipeDeleteBackground
		else -> Color.Transparent
	}
	
	val icon = when (swipeDirection) {
		SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
		else -> null
	}
	
	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(backgroundColor, RoundedCornerShape(Dimensions.CornerRadius.medium))
			.padding(horizontal = Dimensions.Padding.extraLarge),
		contentAlignment = Alignment.CenterEnd
	) {
		icon?.let {
			Icon(
				imageVector = it,
				contentDescription = "删除",
				tint = customColors.scheduleSwipeDeleteIcon,
				modifier = Modifier.padding(Dimensions.Padding.standard)
			)
		}
	}
}

/**
 * 日程卡片内容
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ScheduleItemContent(
	itemData: ScheduleItemData,
	onChecked: () -> Unit,
	onLongPress: (() -> Unit)? = null,
	onReminderTest: (() -> Unit)? = null
) {
	val customColors = MaterialTheme.customColors
	
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.then(
				if (onLongPress != null) {
					Modifier.combinedClickable(
						onClick = { },
						onLongClick = onLongPress
					)
				} else {
					Modifier
				}
			),
		shape = RoundedCornerShape(Dimensions.CornerRadius.medium),
		colors = CardDefaults.cardColors(
			containerColor = customColors.scheduleCardBackground
		),
		elevation = CardDefaults.cardElevation(
			defaultElevation = Dimensions.Elevation.small
		)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(Dimensions.Padding.medium),
			horizontalArrangement = Arrangement.Start,
			verticalAlignment = Alignment.CenterVertically
		) {
			// 复选框
			Checkbox(
				checked = itemData.isChecked,
				onCheckedChange = { onChecked() },
				colors = CheckboxDefaults.colors(
					checkedColor = customColors.scheduleCheckboxChecked,
					uncheckedColor = customColors.scheduleCheckboxUnchecked,
					checkmarkColor = customColors.scheduleCheckboxCheckmark
				)
			)
			
			Column(
				modifier = Modifier
					.weight(1f)
					.padding(start = Dimensions.Padding.tiny),
				verticalArrangement = Arrangement.spacedBy(Dimensions.Padding.tiny)
			) {
				// 标题文本
				Text(
					text = itemData.title,
					style = Typography.titleSmall,
					fontWeight = FontWeight.Medium,
					color = if (itemData.isChecked) {
						customColors.scheduleCompletedText
					} else {
						customColors.scheduleUncompletedText
					},
					textDecoration = if (itemData.isChecked) {
						TextDecoration.LineThrough
					} else {
						TextDecoration.None
					}
				)
				
				// 描述文本
				if (itemData.desc.isNotBlank()) {
					Text(
						text = itemData.desc,
						fontSize = 12.sp,
						color = customColors.scheduleDateText,
						maxLines = 1
					)
				}
			}
			
			// 提醒图标（如果启用了提醒）
			if (itemData.reminderEnabled && onReminderTest != null) {
				androidx.compose.material3.IconButton(
					onClick = onReminderTest,
					modifier = Modifier.padding(start = Dimensions.Padding.small)
				) {
					Icon(
						imageVector = Icons.Default.Notifications,
						contentDescription = "测试提醒",
						tint = customColors.scheduleCheckboxChecked
					)
				}
			}
		}
	}
}


@Composable
@Preview(showBackground = true)
fun PreviewScheduleItem() {
	MyCalendarTheme {
		Column {
			ScheduleItem(
				itemData = ScheduleItemData(
					title = "团队会议",
					date = LocalDate.now(),
					desc = "讨论项目进度和下周计划",
					isChecked = false
				),
				onChecked = {},
				onDelete = {}
			)
			
			ScheduleItem(
				itemData = ScheduleItemData(
					title = "完成设计稿",
					date = LocalDate.now(),
					desc = "UI设计和交互稿",
					isChecked = true
				),
				onChecked = {},
				onDelete = {}
			)
		}
	}
}

