package cn.szu.blankxiao.mycalendar.di

import cn.szu.blankxiao.mycalendar.data.TokenManager
import cn.szu.blankxiao.mycalendar.data.local.datastore.TokenStorage
import cn.szu.blankxiao.mycalendar.remote.AuthTokenPlugin
import cn.szu.blankxiao.mycalendar.remote.auth.AuthApiClient
import cn.szu.blankxiao.mycalendar.remote.calendar.ScheduleApiClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * 网络模块
 * 提供 Ktor HttpClient（含 Token 拦截器）和 API 客户端的依赖注入
 */
val networkModule = module {

    single<TokenStorage> { TokenManager(androidContext()) }

    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
            isLenient = true
            prettyPrint = false
            explicitNulls = false
        }
    }

    single {
        val tokenStorage = get<TokenStorage>()
        val config = get<AndroidConfig>()
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(get<Json>())
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        if (config.enableLogging) {
                            android.util.Log.d("Ktor", message)
                        }
                    }
                }
                level = if (config.enableLogging) LogLevel.BODY else LogLevel.NONE
            }
            install(AuthTokenPlugin) {
                tokenProvider = { tokenStorage.getToken() }
            }
        }
    }

    single {
        val config = get<AndroidConfig>()
        AuthApiClient(
            baseUrl = config.baseUrl,
            httpClient = get()
        )
    }

    single {
        val config = get<AndroidConfig>()
        ScheduleApiClient(
            baseUrl = config.baseUrl,
            httpClient = get()
        )
    }
}
