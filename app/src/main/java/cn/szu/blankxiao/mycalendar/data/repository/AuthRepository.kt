package cn.szu.blankxiao.mycalendar.data.repository

import cn.szu.blankxiao.mycalendar.data.local.datastore.TokenManager
import cn.szu.blankxiao.mycalendar.data.local.datastore.UserInfo
import cn.szu.blankxiao.mycalendar.remote.auth.model.LoginRequest
import cn.szu.blankxiao.mycalendar.remote.auth.model.LoginResponse
import cn.szu.blankxiao.mycalendar.remote.auth.model.RegisterRequest
import cn.szu.blankxiao.mycalendar.remote.auth.model.SendCodeRequest
import cn.szu.blankxiao.mycalendar.remote.auth.model.UserInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import cn.szu.blankxiao.mycalendar.remote.auth.api.DefaultApi as AuthApi

/**
 * 认证仓库
 * 处理登录、注册、注销等认证相关逻辑
 * 使用OpenAPI生成的API接口
 */
class AuthRepository(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {

    /**
     * 是否已登录
     */
    val isLoggedIn: Flow<Boolean> = tokenManager.isLoggedIn

    /**
     * 用户信息
     */
    val userInfo: Flow<UserInfo?> = tokenManager.userInfo

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
        return withContext(Dispatchers.IO) {
            try {
                val response = authApi.login(request)
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.success == true && result.data != null) {
                        // 保存登录信息
                        val loginResponse = result.data
                        tokenManager.saveLoginInfo(
                            token = loginResponse.token ?: "",
                            userId = loginResponse.userId ?: 0L,
                            username = loginResponse.username ?: "",
                            email = loginResponse.email ?: ""
                        )
                        Result.success(loginResponse)
                    } else {
                        Result.failure(Exception(result?.info ?: "登录失败"))
                    }
                } else {
                    Result.failure(Exception("网络请求失败: ${response.code()}"))
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
        return withContext(Dispatchers.IO) {
            try {
                val request = RegisterRequest(
                    email = email,
                    code = code,
                    username = username,
                    password = password
                )
                val response = authApi.register(request)
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.success == true && result.data != null) {
                        // 注册成功后自动登录，保存登录信息
                        val loginResponse = result.data
                        tokenManager.saveLoginInfo(
                            token = loginResponse.token ?: "",
                            userId = loginResponse.userId ?: 0L,
                            username = loginResponse.username ?: "",
                            email = loginResponse.email ?: ""
                        )
                        Result.success(loginResponse)
                    } else {
                        Result.failure(Exception(result?.info ?: "注册失败"))
                    }
                } else {
                    Result.failure(Exception("网络请求失败: ${response.code()}"))
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
        return withContext(Dispatchers.IO) {
            try {
                val request = SendCodeRequest(email = email, type = type.value)
                val response = authApi.sendCode(request)
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.success == true) {
                        Result.success(true)
                    } else {
                        Result.failure(Exception(result?.info ?: "发送验证码失败"))
                    }
                } else {
                    Result.failure(Exception("网络请求失败: ${response.code()}"))
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
        return withContext(Dispatchers.IO) {
            try {
                // 调用后端注销接口
                authApi.logout()
                // 清除本地登录信息
                tokenManager.clearLoginInfo()
                Result.success(true)
            } catch (e: Exception) {
                // 即使网络失败也清除本地登录信息
                tokenManager.clearLoginInfo()
                Result.success(true)
            }
        }
    }

    /**
     * 获取用户信息
     */
    suspend fun getUserInfo(): Result<UserInfoResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApi.getUserInfo()
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.success == true && result.data != null) {
                        Result.success(result.data)
                    } else {
                        Result.failure(Exception(result?.info ?: "获取用户信息失败"))
                    }
                } else {
                    Result.failure(Exception("网络请求失败: ${response.code()}"))
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

