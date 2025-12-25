package cn.szu.blankxiao.mycalendar.di

import cn.szu.blankxiao.mycalendar.data.local.datastore.TokenManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import cn.szu.blankxiao.mycalendar.remote.auth.api.DefaultApi as AuthApi
import cn.szu.blankxiao.mycalendar.remote.calendar.api.DefaultApi as ScheduleApi

/**
 * 网络模块
 * 提供Retrofit和API服务的依赖注入
 */
val networkModule = module {
    
    // TokenManager
    single { TokenManager(androidContext()) }
    
    // Kotlinx Serialization Json配置
    single {
        Json {
            ignoreUnknownKeys = true           // 忽略未知字段
            coerceInputValues = true           // 强制转换输入值
            encodeDefaults = true              // 编码默认值
            isLenient = true                   // 宽松模式
            prettyPrint = false                // 不格式化输出
            explicitNulls = false              // 不显式编码null
        }
    }
    
    // Token拦截器 - 自动添加Authorization header
    single<Interceptor> {
        val tokenManager: TokenManager = get()
        Interceptor { chain ->
            val originalRequest = chain.request()
            
            // 获取token（如果有）
            val token = runBlocking { tokenManager.getToken() }
            
            val newRequest = if (!token.isNullOrEmpty()) {
                originalRequest.newBuilder()
                    .header("Authorization", token)
                    .build()
            } else {
                originalRequest
            }
            
            chain.proceed(newRequest)
        }
    }
    
    // OkHttp Client（带Token拦截器）
    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val tokenInterceptor: Interceptor = get()
        
        OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)      // Token拦截器
            .addInterceptor(loggingInterceptor)    // 日志拦截器
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    // Retrofit - 通过Gateway统一访问
    single {
        val contentType = "application/json".toMediaType()
        
        Retrofit.Builder()
            .baseUrl("https://api.blankxiao.online/")
            .client(get())
            .addConverterFactory(get<Json>().asConverterFactory(contentType))
            .build()
    }
    
    // Auth API服务（remote.auth包）
    single<AuthApi> {
        get<Retrofit>().create(AuthApi::class.java)
    }
    
    // Schedule API服务（remote.calendar包）
    single<ScheduleApi> {
        get<Retrofit>().create(ScheduleApi::class.java)
    }
}
