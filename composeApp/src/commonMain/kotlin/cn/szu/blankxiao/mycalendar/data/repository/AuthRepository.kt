package cn.szu.blankxiao.mycalendar.data.repository

import cn.szu.blankxiao.mycalendar.data.local.datastore.TokenStorage
import cn.szu.blankxiao.mycalendar.data.local.datastore.UserInfo
import cn.szu.blankxiao.mycalendar.remote.auth.AuthApiClient
import cn.szu.blankxiao.mycalendar.remote.auth.model.LoginRequest
import cn.szu.blankxiao.mycalendar.remote.auth.model.LoginResponse
import cn.szu.blankxiao.mycalendar.remote.auth.model.RegisterRequest
import cn.szu.blankxiao.mycalendar.remote.auth.model.SendCodeRequest
import cn.szu.blankxiao.mycalendar.remote.auth.model.UserInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * 认证仓库
 * 处理登录、注册、注销等认证相关逻辑
 * 使用 Ktor 实现的 API 客户端
 */
class AuthRepository(
    private val authApi: AuthApiClient,
    private val tokenStorage: TokenStorage
) {

    val isLoggedIn: Flow<Boolean> = tokenStorage.isLoggedIn

    val userInfo: Flow<UserInfo?> = tokenStorage.userInfo

    /**
     * 邮箱验证码登录
     */
    suspend fun loginWithCode(email: String, code: String): Result<LoginResponse> {
        return login(LoginRequest(
            email = email,
            code = code,
            loginType = LoginType.EMAIL_CODE.value
        ))
    }

    /**
     * 邮箱密码登录
     */
    suspend fun loginWithPassword(email: String, password: String): Result<LoginResponse> {
        return login(LoginRequest(
            email = email,
            password = password,
            loginType = LoginType.EMAIL_PASSWORD.value
        ))
    }

    /**
     * 登录
     */
    private suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return withContext(Dispatchers.Default) {
            try {
                val result = authApi.login(request)
                val data = result.data
                if (result.success == true && data != null) {
                    tokenStorage.saveLoginInfo(
                        token = data.token ?: "",
                        userId = data.userId ?: 0L,
                        username = data.username ?: "",
                        email = data.email ?: ""
                    )
                    Result.success(data)
                } else {
                    Result.failure(Exception(result.info ?: "登录失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 注册
     */
    suspend fun register(
        email: String,
        code: String,
        username: String,
        password: String
    ): Result<LoginResponse> {
        return withContext(Dispatchers.Default) {
            try {
                val request = RegisterRequest(
                    email = email,
                    code = code,
                    username = username,
                    password = password
                )
                val result = authApi.register(request)
                val data = result.data
                if (result.success == true && data != null) {
                    tokenStorage.saveLoginInfo(
                        token = data.token ?: "",
                        userId = data.userId ?: 0L,
                        username = data.username ?: "",
                        email = data.email ?: ""
                    )
                    Result.success(data)
                } else {
                    Result.failure(Exception(result.info ?: "注册失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 发送验证码
     */
    suspend fun sendCode(email: String, type: CodeType): Result<Boolean> {
        return withContext(Dispatchers.Default) {
            try {
                val request = SendCodeRequest(email = email, type = type.value)
                val result = authApi.sendCode(request)
                if (result.success == true) {
                    Result.success(true)
                } else {
                    Result.failure(Exception(result.info ?: "发送验证码失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 注销
     */
    suspend fun logout(): Result<Boolean> {
        return withContext(Dispatchers.Default) {
            try {
                authApi.logout()
                tokenStorage.clearLoginInfo()
                Result.success(true)
            } catch (e: Exception) {
                tokenStorage.clearLoginInfo()
                Result.success(true)
            }
        }
    }

    /**
     * 获取用户信息
     */
    suspend fun getUserInfo(): Result<UserInfoResponse> {
        return withContext(Dispatchers.Default) {
            try {
                val result = authApi.getUserInfo()
                val data = result.data
                if (result.success == true && data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception(result.info ?: "获取用户信息失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

// ============ Login Type Enum ============

enum class LoginType(val value: String) {
    EMAIL_CODE("EMAIL_CODE"),
    EMAIL_PASSWORD("EMAIL_PASSWORD")
}

enum class CodeType(val value: String) {
    REGISTER("REGISTER"),
    LOGIN("LOGIN")
}
