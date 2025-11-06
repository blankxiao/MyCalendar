package cn.szu.blankxiao.mycalendar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * 主题配置文件
 * 可以自动支持深色/浅色模式切换
 */

@Composable
fun MyCalendarTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),  // 自动检测系统深色模式
	content: @Composable () -> Unit
) {
	// 根据深色模式选择对应的自定义业务颜色
	val customColors = if (darkTheme) {
		DarkCustomColors
	} else {
		LightCustomColors
	}

	// 提供自定义颜色到组合树
	CompositionLocalProvider(LocalCustomColors provides customColors) {
		// 应用主题
		MaterialTheme(
			typography = Typography,  // 字体排版
			shapes = Shapes,          // 形状规范
			content = content
		)
	}
}