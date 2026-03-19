@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package cn.szu.blankxiao.mycalendar.model.settings

import cn.szu.blankxiao.mycalendar.data.local.datastore.ThemeStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.NSUserDefaults

/**
 * 主题存储的 iOS 实现
 * 使用 NSUserDefaults
 */
class ThemeSettingsIos : ThemeStorage {

    private val prefs = NSUserDefaults.standardUserDefaults

    private fun readThemeMode(): ThemeMode = try {
        val name = prefs.stringForKey(KEY_THEME_MODE) ?: ThemeMode.SYSTEM.name
        ThemeMode.valueOf(name)
    } catch (e: IllegalArgumentException) {
        ThemeMode.SYSTEM
    }

    private val _themeMode = MutableStateFlow(readThemeMode())
    override val themeMode: Flow<ThemeMode> = _themeMode.asStateFlow()

    override suspend fun setThemeMode(mode: ThemeMode) {
        prefs.setObject(mode.name, forKey = KEY_THEME_MODE)
        prefs.synchronize()
        _themeMode.value = mode
    }

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
    }
}
