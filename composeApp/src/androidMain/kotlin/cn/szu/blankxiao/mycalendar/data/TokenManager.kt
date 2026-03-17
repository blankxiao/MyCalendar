package cn.szu.blankxiao.mycalendar.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import cn.szu.blankxiao.mycalendar.data.local.datastore.TokenStorage
import cn.szu.blankxiao.mycalendar.data.local.datastore.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Token 存储的 Android 实现
 * 使用 DataStore Preferences 持久化登录信息
 */
class TokenManager(private val context: Context) : TokenStorage {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

        private val KEY_TOKEN = stringPreferencesKey("token")
        private val KEY_USER_ID = longPreferencesKey("user_id")
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_EMAIL = stringPreferencesKey("email")
    }

    override suspend fun saveLoginInfo(token: String, userId: Long, username: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_USER_ID] = userId
            prefs[KEY_USERNAME] = username
            prefs[KEY_EMAIL] = email
        }
    }

    override suspend fun clearLoginInfo() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_TOKEN)
            prefs.remove(KEY_USER_ID)
            prefs.remove(KEY_USERNAME)
            prefs.remove(KEY_EMAIL)
        }
    }

    override suspend fun getToken(): String? {
        return context.dataStore.data.first()[KEY_TOKEN]
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_TOKEN]
    }

    override val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        !prefs[KEY_TOKEN].isNullOrEmpty()
    }

    override val userInfo: Flow<UserInfo?> = context.dataStore.data.map { prefs ->
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