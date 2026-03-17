package cn.szu.blankxiao.mycalendar.ui.util

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

/**
 * 窗口宽度断点（与 Material WindowSizeClass 对齐）
 * - Compact: < 600dp（手机竖屏）
 * - Medium: 600dp ~ 840dp（平板竖屏）
 * - Expanded: >= 840dp（平板横屏、PC）
 */
enum class WindowWidthClass {
    Compact,
    Medium,
    Expanded
}

/**
 * 根据当前配置的屏幕宽度获取 WindowWidthClass
 * 使用 expect/actual 实现，Android 用 LocalConfiguration，JVM 用窗口尺寸
 */
@Composable
expect fun currentWindowWidthClass(): WindowWidthClass

/**
 * 根据宽度 Dp 计算 WindowWidthClass（用于 BoxWithConstraints 等场景）
 * 使用 value 比较以兼容 Compose Multiplatform JVM
 */
fun windowWidthClassFrom(widthDp: Dp): WindowWidthClass {
    val value = widthDp.value
    return when {
        value < 600f -> WindowWidthClass.Compact
        value < 840f -> WindowWidthClass.Medium
        else -> WindowWidthClass.Expanded
    }
}

/**
 * 是否为紧凑布局
 */
fun WindowWidthClass.isCompact(): Boolean = this == WindowWidthClass.Compact

/**
 * 是否为可显示 Master-Detail 的宽度（Medium 或 Expanded）
 */
fun WindowWidthClass.isWideEnoughForDetail(): Boolean =
    this == WindowWidthClass.Medium || this == WindowWidthClass.Expanded

/**
 * 根据可用宽度自适应布局
 * @param compactContent 紧凑布局（< 600dp）
 * @param expandedContent 宽松布局（>= 600dp）
 */
@Composable
fun AdaptiveLayout(
    compactContent: @Composable () -> Unit,
    expandedContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val widthClass = windowWidthClassFrom(maxWidth)
        if (widthClass.isCompact()) {
            compactContent()
        } else {
            expandedContent()
        }
    }
}
