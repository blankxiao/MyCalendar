package cn.szu.blankxiao.mycalendar.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cn.szu.blankxiao.mycalendar.data.local.datastore.ThemeStorage
import cn.szu.blankxiao.mycalendar.model.settings.ThemeMode
import cn.szu.blankxiao.mycalendar.ui.screen.auth.LoginScreen
import cn.szu.blankxiao.mycalendar.ui.screen.auth.RegisterScreen
import cn.szu.blankxiao.mycalendar.ui.screen.main.MainScreen
import cn.szu.blankxiao.mycalendar.ui.screen.settings.SettingsScreen
import cn.szu.blankxiao.mycalendar.ui.theme.MyCalendarTheme
import cn.szu.blankxiao.mycalendar.viewmodel.AuthViewModel
import cn.szu.blankxiao.mycalendar.viewmodel.ScheduleViewModel
import kotlinx.datetime.LocalDate
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * iOS 应用根内容
 * 简单栈式导航，根据登录状态切换主屏/登录屏
 */
@Composable
fun IosAppContent(
    modifier: Modifier = Modifier,
    themeStorage: ThemeStorage = koinInject(),
    scheduleViewModel: ScheduleViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel()
) {
    val themeMode by themeStorage.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    val navStack = remember { mutableStateListOf<IosScreen>() }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        if (navStack.isEmpty()) {
            navStack.add(if (isLoggedIn) IosScreen.Main else IosScreen.Login)
        }
    }

    // 监听登录状态变化，登录成功后重置导航栈
    androidx.compose.runtime.LaunchedEffect(isLoggedIn) {
        if (isLoggedIn && navStack.any { it == IosScreen.Login || it == IosScreen.Register }) {
            navStack.clear()
            navStack.add(IosScreen.Main)
        }
    }

    MyCalendarTheme(themeMode = themeMode) {
        when (val current = navStack.lastOrNull()) {
            IosScreen.Main -> MainScreen(
                modifier = modifier.fillMaxSize(),
                viewModel = scheduleViewModel,
                onNavigateToSettings = { navStack.add(IosScreen.Settings) },
                onNavigateToDayView = { date: LocalDate -> /* 暂不实现日视图 */ }
            )
            IosScreen.Settings -> SettingsScreen(
                viewModel = scheduleViewModel,
                themeStorage = themeStorage,
                onNavigateBack = { if (navStack.size > 1) navStack.removeLast() },
                onNavigateToDataManagement = { },
                onNavigateToLogin = {
                    navStack.clear()
                    navStack.add(IosScreen.Login)
                },
                onShowMessage = { /* iOS 可后续接入 Snackbar */ },
                showDataManagement = false,
                authViewModel = authViewModel
            )
            IosScreen.Login -> LoginScreen(
                onNavigateBack = { /* 根屏无返回 */ },
                onNavigateToRegister = { navStack.add(IosScreen.Register) },
                onLoginSuccess = {
                    navStack.clear()
                    navStack.add(IosScreen.Main)
                },
                viewModel = authViewModel
            )
            IosScreen.Register -> RegisterScreen(
                onNavigateBack = { navStack.removeLast() },
                onNavigateToLogin = { navStack.removeLast() },
                onRegisterSuccess = {
                    navStack.clear()
                    navStack.add(IosScreen.Main)
                },
                viewModel = authViewModel
            )
            null -> MainScreen(
                modifier = modifier.fillMaxSize(),
                viewModel = scheduleViewModel,
                onNavigateToSettings = { navStack.add(IosScreen.Settings) },
                onNavigateToDayView = { }
            )
        }
    }
}

private sealed interface IosScreen {
    data object Main : IosScreen
    data object Settings : IosScreen
    data object Login : IosScreen
    data object Register : IosScreen
}
