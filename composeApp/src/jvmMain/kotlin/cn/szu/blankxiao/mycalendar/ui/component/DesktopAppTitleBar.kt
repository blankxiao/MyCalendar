package cn.szu.blankxiao.mycalendar.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.WindowState

/** 统一标题栏高度，与系统风格一致 */
private val TitleBarHeight = 36.dp

/**
 * 统一应用标题栏：登录/主界面位置一致，仅风格统一
 * - 左侧：可拖拽区域（填满剩余空间）
 * - 右侧：窗口控制按钮（最小化/最大化/关闭）独立右侧
 */
@Composable
fun DesktopAppTitleBar(
    windowScope: WindowScope,
    windowState: WindowState,
    onCloseRequest: () -> Unit,
    modifier: Modifier = Modifier,
    leftContent: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(TitleBarHeight)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(start = 12.dp, end = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 可拖拽区域：weight(1f) 必须加在 Row 的直接子项上，否则无效
        // 若加在内部 Box 上，其父级是 WindowDraggableArea 的 Box，weight 不生效，会挤掉右侧按钮
        windowScope.WindowDraggableArea(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(TitleBarHeight),
                contentAlignment = Alignment.CenterStart
            ) {
                leftContent()
            }
        }
        // 窗口控制：最小化、最大化、关闭（Row 直接子项，固定宽度）
        DesktopWindowControls(
            windowState = windowState,
            onCloseRequest = onCloseRequest,
            modifier = Modifier.padding(start = 4.dp),
            iconTint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    HorizontalDivider()
}
