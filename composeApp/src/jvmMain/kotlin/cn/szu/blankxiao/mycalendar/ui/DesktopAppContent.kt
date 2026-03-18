package cn.szu.blankxiao.mycalendar.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cn.szu.blankxiao.mycalendar.ui.screen.auth.AuthViewModel
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.WindowState
import org.koin.compose.viewmodel.koinViewModel

/**
 * PC 端应用内容：根据登录状态切换主界面/登录界面
 * 支持跳过登录进入本地模式，数据仅保存到本机
 * 统一标题栏：登录/主界面位置一致，仅风格变换，功能一致
 */
@Composable
fun DesktopAppContent(
    windowScope: WindowScope,
    windowState: WindowState,
    onCloseRequest: () -> Unit,
    authViewModel: AuthViewModel = koinViewModel()
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    var isLocalOnly by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // 统一标题栏：始终同位置，可拖拽，窗口控制独立右侧
        DesktopAppTitleBar(
            windowScope = windowScope,
            windowState = windowState,
            onCloseRequest = onCloseRequest,
            leftContent = {
                Text(
                    text = "MyCalendar",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        )

        if (isLoggedIn || isLocalOnly) {
            DesktopMainScreen(
                modifier = Modifier.fillMaxWidth().weight(1f),
                isLocalOnly = isLocalOnly,
                onLogout = {
                    if (isLocalOnly) isLocalOnly = false
                    else authViewModel.logout()
                }
            )
        } else {
            DesktopAuthScreen(
                modifier = Modifier.fillMaxWidth().weight(1f),
                onSkipLogin = { isLocalOnly = true }
            )
        }
    }
}
