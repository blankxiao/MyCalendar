package cn.szu.blankxiao.mycalendar.ui.component.schedule

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Notifications
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
import cn.szu.blankxiao.mycalendar.model.schedule.ScheduleItemData
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme
import cn.szu.blankxiao.mycalendar.ui.theme.Typography
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

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
	onLongPress: (() -> Unit)? = null
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
				onLongPress = onLongPress
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
	onLongPress: (() -> Unit)? = null
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
		shape = RoundedCornerShape(Dimensions.CornerRadius.small),
		colors = CardDefaults.cardColors(
			containerColor = customColors.surface
		),
		elevation = CardDefaults.cardElevation(
			defaultElevation = Dimensions.Elevation.none
		)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(
					horizontal = Dimensions.Padding.medium,
					vertical = Dimensions.Padding.small
				),
			horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.small),
			verticalAlignment = Alignment.CenterVertically
		) {
			// 状态指示器 - 简约圆点
			Box(
				modifier = Modifier
					.size(Dimensions.Size.tiny)
					.background(
						color = if (itemData.isChecked) 
							customColors.success 
						else 
							customColors.buttonPrimaryBackground,
						shape = RoundedCornerShape(50)
					)
			)
			
			// 内容区域
			Column(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.tiny)
			) {
				// 标题行（含提醒标记）
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.tiny)
				) {
					Text(
						text = itemData.title,
						style = Typography.bodyMedium,
						fontWeight = if (itemData.isChecked) FontWeight.Normal else FontWeight.Medium,
						color = if (itemData.isChecked) 
							customColors.textTertiary 
						else 
							customColors.textPrimary,
						textDecoration = if (itemData.isChecked) 
							TextDecoration.LineThrough 
						else 
							TextDecoration.None,
						modifier = Modifier.weight(1f, fill = false)
					)
					
					// 提醒指示器
					if (itemData.reminderEnabled) {
						Icon(
							imageVector = Icons.Outlined.Notifications,
							contentDescription = "已设置提醒",
							modifier = Modifier.size(Dimensions.IconSize.tiny),
							tint = customColors.textSecondary
						)
					}
				}
				
				// 描述（如有）
				if (itemData.description.isNotBlank()) {
					Text(
						text = itemData.description,
						style = Typography.bodySmall,
						color = customColors.textSecondary,
						maxLines = 1
					)
				}
			}
			
			// 完成按钮
			Checkbox(
				checked = itemData.isChecked,
				onCheckedChange = { onChecked() },
				colors = CheckboxDefaults.colors(
					checkedColor = customColors.success,
					uncheckedColor = customColors.outline,
					checkmarkColor = customColors.onSuccess
				)
			)
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
					date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
					description = "讨论项目进度和下周计划",
					isChecked = false
				),
				onChecked = {},
				onDelete = {}
			)
			
			ScheduleItem(
				itemData = ScheduleItemData(
					title = "完成设计稿",
					date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
					description = "UI设计和交互稿",
					isChecked = true
				),
				onChecked = {},
				onDelete = {}
			)
		}
	}
}

