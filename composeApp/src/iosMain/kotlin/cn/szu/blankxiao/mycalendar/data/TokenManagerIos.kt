@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package cn.szu.blankxiao.mycalendar.data

import cn.szu.blankxiao.mycalendar.data.local.datastore.TokenStorage
import cn.szu.blankxiao.mycalendar.data.local.datastore.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import platform.Foundation.NSUserDefaults

/**
 * Token 存储的 iOS 实现
 * 使用 NSUserDefaults 持久化登录信息
 */
class TokenManagerIos : TokenStorage {

    private val prefs = NSUserDefaults.standardUserDefaults

    private fun readUserInfo(): UserInfo? {
        val token = prefs.stringForKey(KEY_TOKEN)
        if (token.isNullOrEmpty()) return null
        val userIdStr = prefs.stringForKey(KEY_USER_ID) ?: "0"
        return UserInfo(
            userId = userIdStr.toLongOrNull() ?: 0L,
            username = prefs.stringForKey(KEY_USERNAME) ?: "",
            email = prefs.stringForKey(KEY_EMAIL) ?: "",
            token = token
        )
    }

    private val _userInfo = MutableStateFlow(readUserInfo())
    override val userInfo: Flow<UserInfo?> = _userInfo.asStateFlow()
    override val isLoggedIn: Flow<Boolean> = _userInfo.asStateFlow().map { it != null }

    override suspend fun saveLoginInfo(token: String, userId: Long, username: String, email: String) {
        prefs.setObject(token, forKey = KEY_TOKEN)
        prefs.setObject(userId.toString(), forKey = KEY_USER_ID)
        prefs.setObject(username, forKey = KEY_USERNAME)
        prefs.setObject(email, forKey = KEY_EMAIL)
        prefs.synchronize()
        _userInfo.value = UserInfo(userId, username, email, token)
    }

    override suspend fun clearLoginInfo() {
        prefs.removeObjectForKey(KEY_TOKEN)
        prefs.removeObjectForKey(KEY_USER_ID)
        prefs.removeObjectForKey(KEY_USERNAME)
        prefs.removeObjectForKey(KEY_EMAIL)
        prefs.synchronize()
        _userInfo.value = null
    }

    override suspend fun getToken(): String? {
        return prefs.stringForKey(KEY_TOKEN)?.takeIf { it.isNotEmpty() }
    }

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
    }
}
