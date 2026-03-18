package cn.szu.blankxiao.mycalendar.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.outlined.FullscreenExit
import androidx.compose.material.icons.outlined.Minimize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.foundation.clickable

/** Windows 风格关闭按钮悬停红色 */
private val CloseHoverRed = Color(0xFFE81123)

/**
 * 窗口控制按钮组（Windows 风格）
 * - 最小化/最大化：hover 时背景变浅
 * - 关闭：hover 时背景变红、图标变白
 */
@Composable
fun DesktopWindowControls(
    windowState: WindowState,
    onCloseRequest: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 最小化
        WindowControlButton(
            onClick = { windowState.isMinimized = true },
            icon = { tint -> Icon(Icons.Outlined.Minimize, contentDescription = "最小化", modifier = Modifier.size(16.dp), tint = tint) },
            hoverBackground = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            iconTint = iconTint
        )
        // 最大化/还原
        WindowControlButton(
            onClick = {
                windowState.placement = when (windowState.placement) {
                    WindowPlacement.Maximized -> WindowPlacement.Floating
                    else -> WindowPlacement.Maximized
                }
            },
            icon = { tint ->
                Icon(
                    imageVector = if (windowState.placement == WindowPlacement.Maximized)
                        Icons.Outlined.FullscreenExit else Icons.Filled.Fullscreen,
                    contentDescription = if (windowState.placement == WindowPlacement.Maximized) "还原" else "最大化",
                    modifier = Modifier.size(16.dp),
                    tint = tint
                )
            },
            hoverBackground = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            iconTint = iconTint
        )
        // 关闭（hover 红色）
        WindowControlButton(
            onClick = onCloseRequest,
            icon = { tint -> Icon(Icons.Filled.Close, contentDescription = "关闭", modifier = Modifier.size(16.dp), tint = tint) },
            hoverBackground = CloseHoverRed,
            iconTint = iconTint,
            hoverIconTint = Color.White
        )
    }
}

@Composable
private fun WindowControlButton(
    onClick: () -> Unit,
    icon: @Composable (tint: Color) -> Unit,
    hoverBackground: Color,
    iconTint: Color,
    hoverIconTint: Color = iconTint,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val tint = if (isHovered && hoverIconTint != iconTint) hoverIconTint else iconTint

    Box(
        modifier = modifier
            .size(46.dp, 32.dp)
            .clip(RoundedCornerShape(2.dp))
            .hoverable(interactionSource = interactionSource)
            .clickable(onClick = onClick)
            .then(
                if (isHovered) Modifier.background(hoverBackground)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        icon(tint)
    }
}
