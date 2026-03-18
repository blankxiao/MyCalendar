package cn.szu.blankxiao.mycalendar.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cn.szu.blankxiao.mycalendar.data.local.datastore.ThemeStorage
import cn.szu.blankxiao.mycalendar.model.settings.ThemeMode
import org.koin.compose.koinInject

/**
 * PC 端主题：复用 Android 主题，根据 ThemeStorage 切换 LIGHT/DARK/CHRISTMAS/SYSTEM
 */
@Composable
fun DesktopTheme(
    themeStorage: ThemeStorage = koinInject(),
    content: @Composable () -> Unit
) {
    val themeMode by themeStorage.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    MyCalendarTheme(themeMode = themeMode, content = content)
}
