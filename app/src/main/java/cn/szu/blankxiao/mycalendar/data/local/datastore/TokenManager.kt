package cn.szu.blankxiao.mycalendar.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Token管理器
 * 使用DataStore存储用户登录信息
 */
class TokenManager(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")
        
        private val KEY_TOKEN = stringPreferencesKey("token")
        private val KEY_USER_ID = longPreferencesKey("user_id")
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_EMAIL = stringPreferencesKey("email")
    }

    /**
     * 保存登录信息
     */
    suspend fun saveLoginInfo(token: String, userId: Long, username: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_USER_ID] = userId
            prefs[KEY_USERNAME] = username
            prefs[KEY_EMAIL] = email
        }
    }

    /**
     * 清除登录信息
     */
    suspend fun clearLoginInfo() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_TOKEN)
            prefs.remove(KEY_USER_ID)
            prefs.remove(KEY_USERNAME)
            prefs.remove(KEY_EMAIL)
        }
    }

    /**
     * 获取Token
     */
    suspend fun getToken(): String? {
        return context.dataStore.data.first()[KEY_TOKEN]
    }

    /**
     * Token Flow
     */
    val tokenFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_TOKEN]
    }

    /**
     * 是否已登录
     */
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        !prefs[KEY_TOKEN].isNullOrEmpty()
    }

    /**
     * 用户信息Flow
     */
    val userInfo: Flow<UserInfo?> = context.dataStore.data.map { prefs ->
        val token = prefs[KEY_TOKEN]
        if (token.isNullOrEmpty()) {
            null
        } else {
            UserInfo(
                userId = prefs[KEY_USER_ID] ?: 0L,
                username = prefs[KEY_USERNAME] ?: "",
                email = prefs[KEY_EMAIL] ?: "",
                token = token
            )
        }
    }
}

/**
 * 用户信息数据类
 */
data class UserInfo(
    val userId: Long,
    val username: String,
    val email: String,
    val token: String
)

