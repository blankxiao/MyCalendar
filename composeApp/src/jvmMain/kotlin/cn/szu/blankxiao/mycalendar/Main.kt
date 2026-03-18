package cn.szu.blankxiao.mycalendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cn.szu.blankxiao.mycalendar.di.desktopModules
import cn.szu.blankxiao.mycalendar.ui.screen.main.DesktopAppContent
import cn.szu.blankxiao.mycalendar.ui.theme.DesktopTheme
import org.koin.core.context.startKoin

/** 首次启动窗口尺寸：适配月历 7 列 × 6 行 + 侧栏 + 顶栏，尽量无需滚动 */
private val InitialWindowSize = DpSize(880.dp, 780.dp)

@OptIn(ExperimentalMaterial3Api::class)
fun main() {
    startKoin {
        modules(desktopModules)
    }
    application {
        val windowState = rememberWindowState(size = InitialWindowSize)
        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "MyCalendar",
            undecorated = true,
            transparent = true
        ) {
            val scope = this
            DesktopTheme {
                // 单一 Box：圆角 + 阴影 + 背景，避免父子组件边角冲突
                val shape = RoundedCornerShape(12.dp)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 8.dp, bottom = 8.dp)
                        .dropShadow(
                            shape = shape,
                            shadow = Shadow(
                                radius = 6.dp,
                                color = Color.Black,
                                offset = DpOffset(0.dp, 4.dp),
                                alpha = 0.12f
                            )
                        )
                        .clip(shape)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    DesktopAppContent(
                        windowScope = scope,
                        windowState = windowState,
                        onCloseRequest = ::exitApplication
                    )
                }
            }
        }
    }
}
