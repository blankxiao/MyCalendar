package cn.szu.blankxiao.mycalendar.di

import cn.szu.blankxiao.mycalendar.data.TokenManagerDesktop
import cn.szu.blankxiao.mycalendar.data.local.AppDatabase
import cn.szu.blankxiao.mycalendar.data.local.createAppDatabase
import cn.szu.blankxiao.mycalendar.data.local.datastore.TokenStorage
import cn.szu.blankxiao.mycalendar.data.repository.AuthRepository
import cn.szu.blankxiao.mycalendar.data.repository.RemoteScheduleRepository
import cn.szu.blankxiao.mycalendar.data.repository.ScheduleRepositoryEx
import cn.szu.blankxiao.mycalendar.data.repository.ScheduleRepositoryImpl
import cn.szu.blankxiao.mycalendar.data.repository.SyncScheduleRepository
import cn.szu.blankxiao.mycalendar.data.local.datastore.ThemeStorage
import cn.szu.blankxiao.mycalendar.model.settings.ThemeSettingsDesktop
import cn.szu.blankxiao.mycalendar.remote.AuthTokenPlugin
import cn.szu.blankxiao.mycalendar.remote.auth.AuthApiClient
import cn.szu.blankxiao.mycalendar.remote.calendar.ScheduleApiClient
import cn.szu.blankxiao.mycalendar.service.export.IcsScheduleSerializer
import cn.szu.blankxiao.mycalendar.service.export.JsonScheduleSerializer
import cn.szu.blankxiao.mycalendar.service.export.ScheduleStringSerializer
import cn.szu.blankxiao.mycalendar.service.reminder.NoOpReminderScheduler
import cn.szu.blankxiao.mycalendar.service.reminder.ReminderScheduler
import cn.szu.blankxiao.mycalendar.ui.screen.auth.AuthViewModel
import cn.szu.blankxiao.mycalendar.ui.screen.main.ScheduleViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Desktop 平台的 API 配置
 */
object DesktopConfig {
    // 勿加尾部 /，路径拼接为 "$baseUrl/auth/xxx" 会变成 //auth
const val BASE_URL = "https://api.blankxiao.online"
    const val ENABLE_LOGGING = true
}

val databaseModuleDesktop = module {
    single { createAppDatabase() }
    single { get<AppDatabase>().scheduleDao() }
}

val repositoryModuleDesktop = module {
    single<ScheduleRepositoryEx>(named("local")) {
        ScheduleRepositoryImpl(scheduleDao = get())
    }
    single {
        RemoteScheduleRepository(api = get())
    }
    single<ScheduleRepositoryEx> {
        SyncScheduleRepository(
            localRepository = get(named("local")),
            remoteRepository = get(),
            tokenStorage = get()
        )
    }
    single {
        AuthRepository(
            authApi = get(),
            tokenStorage = get()
        )
    }
}

val exportSerializerModuleDesktop = module {
    single<ScheduleStringSerializer>(named("json")) { JsonScheduleSerializer() }
    single<ScheduleStringSerializer>(named("ics")) { IcsScheduleSerializer() }
}

val reminderModuleDesktop = module {
    single<ReminderScheduler> { NoOpReminderScheduler() }
}

val viewModelModuleDesktop = module {
    viewModel {
        ScheduleViewModel(
            repository = get(),
            jsonSerializer = get(named("json")),
            icsSerializer = get(named("ics"))
        )
    }
    viewModel {
        AuthViewModel(authRepository = get())
    }
}

val networkModuleDesktop = module {
    single<TokenStorage> { TokenManagerDesktop() }
    single<ThemeStorage> { ThemeSettingsDesktop() }

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
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(get<Json>())
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        if (DesktopConfig.ENABLE_LOGGING) {
                            println(message)
                        }
                    }
                }
                level = if (DesktopConfig.ENABLE_LOGGING) LogLevel.BODY else LogLevel.NONE
            }
            install(AuthTokenPlugin) {
                tokenProvider = { tokenStorage.getToken() }
            }
        }
    }

    single {
        AuthApiClient(
            baseUrl = DesktopConfig.BASE_URL,
            httpClient = get()
        )
    }

    single {
        ScheduleApiClient(
            baseUrl = DesktopConfig.BASE_URL,
            httpClient = get()
        )
    }
}

val desktopModules = listOf(
    databaseModuleDesktop,
    repositoryModuleDesktop,
    exportSerializerModuleDesktop,
    reminderModuleDesktop,
    viewModelModuleDesktop,
    networkModuleDesktop
)
