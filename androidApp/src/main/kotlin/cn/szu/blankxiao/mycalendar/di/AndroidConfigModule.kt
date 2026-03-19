package cn.szu.blankxiao.mycalendar.di

import cn.szu.blankxiao.mycalendar.BuildConfig
import cn.szu.blankxiao.mycalendar.MainActivity
import cn.szu.blankxiao.mycalendar.service.reminder.ReminderReceiver
import org.koin.core.qualifier.named
import org.koin.dsl.module

val androidConfigModule = module {
    single {
        AndroidConfig(
            baseUrl = BuildKonfig.BASE_URL,
            enableLogging = BuildConfig.ENABLE_LOGGING
        )
    }
    single<Class<*>>(named("launcherActivity")) { MainActivity::class.java }
    single<Class<*>>(named("reminderReceiver")) { ReminderReceiver::class.java }
}
