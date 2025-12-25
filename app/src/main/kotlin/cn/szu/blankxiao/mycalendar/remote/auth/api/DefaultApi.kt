package cn.szu.blankxiao.mycalendar.remote.auth.api

import retrofit2.http.*
import retrofit2.Response

import cn.szu.blankxiao.mycalendar.remote.auth.model.LoginRequest
import cn.szu.blankxiao.mycalendar.remote.auth.model.RegisterRequest
import cn.szu.blankxiao.mycalendar.remote.auth.model.ResultBoolean
import cn.szu.blankxiao.mycalendar.remote.auth.model.ResultLoginResponse
import cn.szu.blankxiao.mycalendar.remote.auth.model.ResultUserInfoResponse
import cn.szu.blankxiao.mycalendar.remote.auth.model.SendCodeRequest

interface DefaultApi {
    /**
     * 获取当前用户信息
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [ResultUserInfoResponse]
     */
    @GET("auth/user-info")
    suspend fun getUserInfo(): Response<ResultUserInfoResponse>

    /**
     * 登录
     * 
     * Responses:
     *  - 200: OK
     *
     * @param loginRequest 
     * @return [ResultLoginResponse]
     */
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<ResultLoginResponse>

    /**
     * 注销登录
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [ResultBoolean]
     */
    @POST("auth/logout")
    suspend fun logout(): Response<ResultBoolean>

    /**
     * 注册
     * 
     * Responses:
     *  - 200: OK
     *
     * @param registerRequest 
     * @return [ResultLoginResponse]
     */
    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<ResultLoginResponse>

    /**
     * 发送邮箱验证码
     * 
     * Responses:
     *  - 200: OK
     *
     * @param sendCodeRequest 
     * @return [ResultBoolean]
     */
    @POST("auth/send-code")
    suspend fun sendCode(@Body sendCodeRequest: SendCodeRequest): Response<ResultBoolean>

}
