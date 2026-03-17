package cn.szu.blankxiao.mycalendar.data.local.datastore

import cn.szu.blankxiao.mycalendar.model.settings.ThemeMode
import kotlinx.coroutines.flow.Flow

/**
 * 主题存储接口
 * 各平台实现：Android 用 DataStore，iOS 用 UserDefaults 等
 */
interface ThemeStorage {

    val themeMode: Flow<ThemeMode>

    suspend fun setThemeMode(mode: ThemeMode)
}
