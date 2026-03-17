package cn.szu.blankxiao.mycalendar.data.local.datastore

import kotlinx.coroutines.flow.Flow

/**
 * Token 存储接口
 * 各平台实现：Android 用 DataStore，iOS 用 Keychain/UserDefaults 等
 */
interface TokenStorage {

    suspend fun getToken(): String?

    suspend fun saveLoginInfo(token: String, userId: Long, username: String, email: String)

    suspend fun clearLoginInfo()

    val isLoggedIn: Flow<Boolean>

    val userInfo: Flow<UserInfo?>
}
