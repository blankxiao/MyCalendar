package cn.szu.blankxiao.mycalendar.remote.auth

import cn.szu.blankxiao.mycalendar.remote.auth.model.LoginRequest
import cn.szu.blankxiao.mycalendar.remote.auth.model.RegisterRequest
import cn.szu.blankxiao.mycalendar.remote.auth.model.ResultBoolean
import cn.szu.blankxiao.mycalendar.remote.auth.model.ResultLoginResponse
import cn.szu.blankxiao.mycalendar.remote.auth.model.ResultUserInfoResponse
import cn.szu.blankxiao.mycalendar.remote.auth.model.SendCodeRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Auth API 客户端（Ktor 实现）
 * Token 由 HttpClient 的拦截器统一添加
 */
class AuthApiClient(
    private val baseUrl: String,
    private val httpClient: HttpClient
) {

    suspend fun getUserInfo(): ResultUserInfoResponse {
        return httpClient.get("$baseUrl/auth/user-info") {
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun login(loginRequest: LoginRequest): ResultLoginResponse {
        return httpClient.post("$baseUrl/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(loginRequest)
        }.body()
    }

    suspend fun logout(): ResultBoolean {
        return httpClient.post("$baseUrl/auth/logout") {
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun register(registerRequest: RegisterRequest): ResultLoginResponse {
        return httpClient.post("$baseUrl/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(registerRequest)
        }.body()
    }

    suspend fun sendCode(sendCodeRequest: SendCodeRequest): ResultBoolean {
        return httpClient.post("$baseUrl/auth/send-code") {
            contentType(ContentType.Application.Json)
            setBody(sendCodeRequest)
        }.body()
    }
}
