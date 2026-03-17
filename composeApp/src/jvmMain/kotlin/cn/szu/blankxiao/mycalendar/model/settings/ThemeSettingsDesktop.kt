package cn.szu.blankxiao.mycalendar.model.settings

import cn.szu.blankxiao.mycalendar.data.local.datastore.ThemeStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.prefs.Preferences

/**
 * 主题存储的 Desktop 实现
 * 使用 Java Preferences API
 */
class ThemeSettingsDesktop : ThemeStorage {

    private val prefs = Preferences.userRoot().node("cn.szu.blankxiao.mycalendar.settings")

    private fun readThemeMode(): ThemeMode = try {
        ThemeMode.valueOf(prefs.get(KEY_THEME_MODE, ThemeMode.SYSTEM.name))
    } catch (e: IllegalArgumentException) {
        ThemeMode.SYSTEM
    }

    private val _themeMode = MutableStateFlow(readThemeMode())
    override val themeMode: Flow<ThemeMode> = _themeMode.asStateFlow()

    override suspend fun setThemeMode(mode: ThemeMode) {
        prefs.put(KEY_THEME_MODE, mode.name)
        prefs.flush()
        _themeMode.value = mode
    }

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
    }
}
