package cn.szu.blankxiao.mycalendar.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cn.szu.blankxiao.mycalendar.ui.screen.auth.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * PC 端应用内容：根据登录状态切换主界面/登录界面
 */
@Composable
fun DesktopAppContent(
    authViewModel: AuthViewModel = koinViewModel()
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    var skipLogin by remember { mutableStateOf(false) }

    if (isLoggedIn || skipLogin) {
        DesktopMainScreen(
            onLogout = {
                if (skipLogin) skipLogin = false else authViewModel.logout()
            }
        )
    } else {
        DesktopAuthScreen(
            onSkipLogin = { skipLogin = true }
        )
    }
}
