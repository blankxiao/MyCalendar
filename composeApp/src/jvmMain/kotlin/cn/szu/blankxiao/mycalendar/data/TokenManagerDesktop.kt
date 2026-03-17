package cn.szu.blankxiao.mycalendar.data

import cn.szu.blankxiao.mycalendar.data.local.datastore.TokenStorage
import cn.szu.blankxiao.mycalendar.data.local.datastore.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.prefs.Preferences

/**
 * Token 存储的 Desktop 实现
 * 使用 Java Preferences API 持久化登录信息
 */
class TokenManagerDesktop : TokenStorage {

    private val prefs = Preferences.userRoot().node("cn.szu.blankxiao.mycalendar.auth")

    private fun readUserInfo(): UserInfo? {
        val token = prefs.get(KEY_TOKEN, null)
        if (token.isNullOrEmpty()) return null
        return UserInfo(
            userId = prefs.getLong(KEY_USER_ID, 0L),
            username = prefs.get(KEY_USERNAME, "") ?: "",
            email = prefs.get(KEY_EMAIL, "") ?: "",
            token = token
        )
    }

    private val _userInfo = MutableStateFlow(readUserInfo())
    override val userInfo: Flow<UserInfo?> = _userInfo.asStateFlow()
    override val isLoggedIn: Flow<Boolean> = _userInfo.asStateFlow().map { it != null }

    override suspend fun saveLoginInfo(token: String, userId: Long, username: String, email: String) {
        prefs.put(KEY_TOKEN, token)
        prefs.putLong(KEY_USER_ID, userId)
        prefs.put(KEY_USERNAME, username)
        prefs.put(KEY_EMAIL, email)
        prefs.flush()
        _userInfo.value = UserInfo(userId, username, email, token)
    }

    override suspend fun clearLoginInfo() {
        prefs.remove(KEY_TOKEN)
        prefs.remove(KEY_USER_ID)
        prefs.remove(KEY_USERNAME)
        prefs.remove(KEY_EMAIL)
        prefs.flush()
        _userInfo.value = null
    }

    override suspend fun getToken(): String? {
        return prefs.get(KEY_TOKEN, null)?.takeIf { it.isNotEmpty() }
    }

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
    }
}
