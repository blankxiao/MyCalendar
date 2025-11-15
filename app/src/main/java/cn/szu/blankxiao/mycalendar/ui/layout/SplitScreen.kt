package cn.szu.blankxiao.mycalendar.ui.layout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.szu.blankxiao.mycalendar.ui.theme.Dimensions
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme
import cn.szu.blankxiao.mycalendar.ui.theme.customColors
import kotlinx.coroutines.launch

/**
 * @author BlankXiao
 * @description 三态底部抽屉组件
 * @date 2025-11-08
 */

/**
 * 底部抽屉的三种状态
 */
enum class BottomSheetState {
	EXPANDED,    // 展开：2/3 屏幕高度
	HALF,        // 半开：1/2 屏幕高度
	COLLAPSED    // 收起：显示少量内容（peek 高度）
}

/**
 * 三态分屏布局组件
 * 
 * 上下分屏布局，抽屉状态改变时两部分内容都会联动响应
 * mainContent 和 sheetContent 根据状态动态调整高度比例
 * 
 * @param currentState 当前抽屉状态（外部控制）
 * @param onStateChange 状态改变回调
 * @param mainContent 主内容区域，接收当前状态作为参数
 * @param sheetContent 抽屉内容，接收当前状态作为参数
 * @param modifier 修饰符
 */
@Composable
fun ThreeStateBottomSheet(
	currentState: BottomSheetState,
	onStateChange: (BottomSheetState) -> Unit,
	mainContent: @Composable (BottomSheetState) -> Unit,
	sheetContent: @Composable ColumnScope.(BottomSheetState) -> Unit,
	modifier: Modifier = Modifier
) {
	val customColors = MaterialTheme.customColors
	val configuration = LocalConfiguration.current
	val density = LocalDensity.current
    // 等价于 val px = configuration.screenHeightDp.dp.toPx(density)
	val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
	
	val scope = rememberCoroutineScope()
	
	// 三种状态对应的高度比例（从底部算起）
	val getHeightForState: (BottomSheetState) -> Float = { state ->
		when (state) {
			BottomSheetState.EXPANDED -> screenHeightPx * 0.6f      // 抽屉占 60%
			BottomSheetState.HALF -> screenHeightPx * 0.4f          // 抽屉占 40%
			BottomSheetState.COLLAPSED -> screenHeightPx * 0.15f    // 抽屉占 15%
		}
	}
	
	val sheetHeightPx = remember { Animatable(getHeightForState(currentState)) }
	
	val currentSheetHeightPx by remember {
		derivedStateOf { sheetHeightPx.value }
	}

	// 当状态改变时，动画到新高度
	LaunchedEffect(currentState) {
		sheetHeightPx.animateTo(
			targetValue = getHeightForState(currentState),
			animationSpec = spring(
				dampingRatio = Spring.DampingRatioMediumBouncy,
				stiffness = Spring.StiffnessMedium
			)
		)
	}
	
	// 转换为 Dp（使用被包装的 State）
	val currentSheetHeight = with(density) { currentSheetHeightPx.toDp() }
	val screenHeight = configuration.screenHeightDp.dp
	val mainContentHeight = screenHeight - currentSheetHeight
	
	Column(
		modifier = modifier
			.fillMaxSize()
			.background(customColors.calendarBackground)
	) {
		// 主内容区域（上半部分）- 高度 = 屏幕高度 - sheet高度（自适应）
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(mainContentHeight)
				.background(customColors.calendarBackground),
			contentAlignment = Alignment.TopCenter
		) {
			mainContent(currentState)
		}
		
		// 底部抽屉（下半部分）- 固定高度，由拖拽控制
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(currentSheetHeight)
				.shadow(elevation = Dimensions.Elevation.large,
					  shape = RoundedCornerShape(
						  topStart = Dimensions.CornerRadius.large,
						  topEnd = Dimensions.CornerRadius.large
					  ))
		) {
			// 内容层
			Surface(
				modifier = Modifier
					.fillMaxSize()
					.pointerInput(currentState) {
						detectVerticalDragGestures(
							onDragStart = {
							},
							onDragEnd = {
								// 根据当前高度决定目标状态
								val currentHeight = sheetHeightPx.value
								val collapsedHeight = getHeightForState(BottomSheetState.COLLAPSED)
								val halfHeight = getHeightForState(BottomSheetState.HALF)
								val expandedHeight = getHeightForState(BottomSheetState.EXPANDED)
								
								val newState = when {
									currentHeight < (collapsedHeight + halfHeight) / 2 -> BottomSheetState.COLLAPSED
									currentHeight < (halfHeight + expandedHeight) / 2 -> BottomSheetState.HALF
									else -> BottomSheetState.EXPANDED
								}
								
								if (newState != currentState) {
									onStateChange(newState)
								} else {
									// 如果状态没变，回弹到当前状态的标准高度
									scope.launch {
										sheetHeightPx.animateTo(
											targetValue = getHeightForState(currentState),
											animationSpec = spring(
												dampingRatio = Spring.DampingRatioMediumBouncy,
												stiffness = Spring.StiffnessHigh
											)
										)
									}
								}
							},
							onVerticalDrag = { _, dragAmount ->
								// 拖拽时实时更新高度（向上拖拽 dragAmount < 0，高度增加）
								scope.launch {
									// 限制拖动的范围
									val newHeight = (sheetHeightPx.value - dragAmount).coerceIn(
										getHeightForState(BottomSheetState.COLLAPSED) * 0.8f,
										getHeightForState(BottomSheetState.EXPANDED) * 1.1f
									)
									sheetHeightPx.snapTo(newHeight)
								}
							}
						)
					},
				shape = RoundedCornerShape(
					topStart = Dimensions.CornerRadius.large,
					topEnd = Dimensions.CornerRadius.large,
					bottomStart = Dimensions.CornerRadius.none,
					bottomEnd = Dimensions.CornerRadius.none
				),
				color = customColors.bottomSheetBackground,
				tonalElevation = Dimensions.Elevation.none
			) {
				Column(
					modifier = Modifier.fillMaxSize()
				) {
					// 顶部拖拽指示器区域
					Box(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = Dimensions.Padding.small),
						contentAlignment = Alignment.Center
					) {
						Box(
							modifier = Modifier
								.size(width = Dimensions.Spacing.extraLarge, height = Dimensions.Divider.thicknessLarge)
								.clip(RoundedCornerShape(Dimensions.CornerRadius.extraLarge))
								.background(customColors.bottomSheetHandle)
						)
					}
					
					// 抽屉内容（传递当前状态）
					sheetContent(currentState)
				}
			}
		}
	}
}

@Composable
@Preview(showBackground = true)
fun PreviewBottomSheet() {
	// 外部控制状态
	var currentState by remember { mutableStateOf(BottomSheetState.HALF) }
	
	MyCalendarTheme {
		ThreeStateBottomSheet(
			currentState = currentState,
			onStateChange = { newState ->
				currentState = newState
			},
			mainContent = { state ->
				// 根据状态显示不同内容
				Box(
					modifier = Modifier
						.fillMaxSize()
						.background(MaterialTheme.customColors.calendarBackground),
					contentAlignment = Alignment.Center
				) {
					Column(
						horizontalAlignment = Alignment.CenterHorizontally,
						verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.small)
					) {
						Text(
							text = when (state) {
								BottomSheetState.COLLAPSED -> "📅 月视图\n(显示详细日历)"
								BottomSheetState.HALF -> "📅 月视图"
								BottomSheetState.EXPANDED -> "📋 周视图"
							},
							style = MaterialTheme.typography.headlineMedium,
							textAlign = TextAlign.Center
						)
						
						Text(
							text = "当前状态: ${state.name}",
							style = MaterialTheme.typography.bodyMedium,
							color = MaterialTheme.customColors.todoListEmptyText
						)
					}
				}
			},
			sheetContent = { state ->
				// 抽屉内容也根据状态变化
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.padding(Dimensions.Padding.standard)
				) {
					Text(
						text = when (state) {
							BottomSheetState.COLLAPSED -> "收起状态"
							BottomSheetState.HALF -> "今日待办"
							BottomSheetState.EXPANDED -> "所有待办"
						},
						style = MaterialTheme.typography.titleLarge
					)
					
					Text(
						text = "向上/下拖拽改变状态\n状态改变时，上下两部分内容都会联动响应",
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.customColors.todoListEmptyText,
						modifier = Modifier.padding(top = Dimensions.Padding.small)
					)
				}
			}
		)
	}
}
