package cn.szu.blankxiao.mycalendar.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 轻量简约尺寸规范
 */

object Dimensions {
	
	// ==================== 间距系统 ====================
	object Spacing {
		val extraSmall: Dp = 4.dp      // 超小间距
		val small: Dp = 8.dp            // 小间距
		val medium: Dp = 16.dp          // 中间距（标准）
		val large: Dp = 24.dp           // 大间距
		val extraLarge: Dp = 32.dp      // 超大间距
		val huge: Dp = 48.dp            // 巨大间距
	}
	
	// ==================== 内边距 ====================
	object Padding {
		val tiny: Dp = 4.dp
		val small: Dp = 8.dp
		val medium: Dp = 12.dp
		val standard: Dp = 16.dp        // 标准内边距
		val large: Dp = 20.dp
		val extraLarge: Dp = 24.dp
	}
	
	// ==================== 圆角半径 ====================
	object CornerRadius {
		val small: Dp = 8.dp            // 小圆角
		val medium: Dp = 12.dp          // 中圆角（标准）
		val large: Dp = 16.dp           // 大圆角
		val extraLarge: Dp = 24.dp      // 超大圆角
		val circle: Dp = 999.dp         // 圆形
	}
	
	// ==================== 阴影高度 ====================
	object Elevation {
		val minimal: Dp = 1.dp          // 极轻阴影
		val small: Dp = 2.dp            // 小阴影（标准）
		val medium: Dp = 4.dp           // 中阴影
		val large: Dp = 8.dp            // 大阴影
		val extraLarge: Dp = 16.dp      // 超大阴影
	}
	
	// ==================== 图标尺寸 ====================
	object IconSize {
		val tiny: Dp = 12.dp
		val small: Dp = 16.dp
		val medium: Dp = 24.dp          // 标准图标
		val large: Dp = 32.dp
		val extraLarge: Dp = 48.dp
		val huge: Dp = 64.dp
	}

	// ==================== 分割线 ====================
	object Divider {
		val thickness: Dp = 1.dp        // 分割线厚度
		val thicknessBold: Dp = 2.dp    // 加粗分割线
	}
}

