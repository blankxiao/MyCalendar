package cn.szu.blankxiao.mycalendar.ui.util

import androidx.compose.runtime.Composable

@Composable
actual fun currentWindowWidthClass(): WindowWidthClass {
    // Desktop 窗口通常较宽，默认 Expanded；实际布局由 AdaptiveLayout/BoxWithConstraints 决定
    return WindowWidthClass.Expanded
}
