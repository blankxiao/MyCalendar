package cn.szu.blankxiao.mycalendar.di

import cn.szu.blankxiao.mycalendar.data.TokenManagerIos
import cn.szu.blankxiao.mycalendar.data.local.AppDatabase
import cn.szu.blankxiao.mycalendar.data.local.createAppDatabase
import cn.szu.blankxiao.mycalendar.data.local.datastore.ThemeStorage
import cn.szu.blankxiao.mycalendar.data.local.datastore.TokenStorage
import cn.szu.blankxiao.mycalendar.data.repository.AuthRepository
import cn.szu.blankxiao.mycalendar.data.repository.RemoteScheduleRepository
import cn.szu.blankxiao.mycalendar.data.repository.ScheduleRepositoryEx
import cn.szu.blankxiao.mycalendar.data.repository.ScheduleRepositoryImpl
import cn.szu.blankxiao.mycalendar.data.repository.SyncScheduleRepository
import cn.szu.blankxiao.mycalendar.model.settings.ThemeSettingsIos
import cn.szu.blankxiao.mycalendar.remote.AuthTokenPlugin
import cn.szu.blankxiao.mycalendar.remote.auth.AuthApiClient
import cn.szu.blankxiao.mycalendar.remote.calendar.ScheduleApiClient
import cn.szu.blankxiao.mycalendar.service.export.IcsScheduleSerializer
import cn.szu.blankxiao.mycalendar.service.export.JsonScheduleSerializer
import cn.szu.blankxiao.mycalendar.service.export.ScheduleStringSerializer
import cn.szu.blankxiao.mycalendar.service.reminder.NoOpReminderScheduler
import cn.szu.blankxiao.mycalendar.service.reminder.ReminderScheduler
import cn.szu.blankxiao.mycalendar.viewmodel.AuthViewModel
import cn.szu.blankxiao.mycalendar.viewmodel.ScheduleViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * iOS 平台的 API 配置
 */
object IosConfig {
    const val ENABLE_LOGGING = true
}

val databaseModuleIos = module {
    single { createAppDatabase() }
    single { get<AppDatabase>().scheduleDao() }
}

val repositoryModuleIos = module {
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

val exportSerializerModuleIos = module {
    single<ScheduleStringSerializer>(named("json")) { JsonScheduleSerializer() }
    single<ScheduleStringSerializer>(named("ics")) { IcsScheduleSerializer() }
}

val reminderModuleIos = module {
    single<ReminderScheduler> { NoOpReminderScheduler() }
}

val viewModelModuleIos = module {
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

val networkModuleIos = module {
    single<TokenStorage> { TokenManagerIos() }
    single<ThemeStorage> { ThemeSettingsIos() }

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
        HttpClient(Darwin) {
            install(ContentNegotiation) {
                json(get<Json>())
            }
            install(AuthTokenPlugin) {
                tokenProvider = { tokenStorage.getToken() }
            }
        }
    }

    single {
        AuthApiClient(
            baseUrl = BuildKonfig.BASE_URL,
            httpClient = get()
        )
    }

    single {
        ScheduleApiClient(
            baseUrl = BuildKonfig.BASE_URL,
            httpClient = get()
        )
    }
}

/**
 * iOS 平台所有 Koin 模块
 * 在 AppDelegate 中通过 startKoin { modules(iosModules) } 初始化
 */
val iosModules = listOf(
    databaseModuleIos,
    repositoryModuleIos,
    exportSerializerModuleIos,
    reminderModuleIos,
    viewModelModuleIos,
    networkModuleIos
)
