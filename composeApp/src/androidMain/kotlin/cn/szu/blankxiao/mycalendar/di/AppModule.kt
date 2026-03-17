package cn.szu.blankxiao.mycalendar.di

import cn.szu.blankxiao.mycalendar.data.local.AppDatabase
import cn.szu.blankxiao.mycalendar.data.local.createAppDatabase
import cn.szu.blankxiao.mycalendar.data.repository.AuthRepository
import cn.szu.blankxiao.mycalendar.data.repository.RemoteScheduleRepository
import cn.szu.blankxiao.mycalendar.data.repository.ScheduleRepositoryEx
import cn.szu.blankxiao.mycalendar.data.repository.ScheduleRepositoryImpl
import cn.szu.blankxiao.mycalendar.data.repository.SyncScheduleRepository
import cn.szu.blankxiao.mycalendar.service.export.IcsScheduleSerializer
import cn.szu.blankxiao.mycalendar.service.export.JsonScheduleSerializer
import cn.szu.blankxiao.mycalendar.service.export.ScheduleStringSerializer
import cn.szu.blankxiao.mycalendar.service.reminder.ReminderManager
import cn.szu.blankxiao.mycalendar.service.reminder.ReminderScheduler
import cn.szu.blankxiao.mycalendar.ui.screen.auth.AuthViewModel
import cn.szu.blankxiao.mycalendar.ui.screen.main.ScheduleViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * @author BlankXiao
 * @description Koin依赖注入模块配置
 * @date 2025-12-11
 * 
 * 配置应用所需的所有依赖注入
 * - 数据库实例（单例）
 * - DAO实例
 * - Repository实例
 * - ViewModel实例
 */

/**
 * 数据库模块
 * 提供Room数据库相关的依赖
 */
val databaseModule = module {
    // 单例：数据库实例
    single {
        createAppDatabase(androidContext())
    }
    
    // 单例：ScheduleDao
    single {
        get<AppDatabase>().scheduleDao()
    }
}

/**
 * 仓库模块
 * 提供Repository层的依赖
 */
val repositoryModule = module {
	// 本地ScheduleRepository
	single<ScheduleRepositoryEx>(named("local")) {
		ScheduleRepositoryImpl(
			scheduleDao = get()
		)
	}

	// 远程ScheduleRepository
	single {
		RemoteScheduleRepository(
			api = get()
		)
	}

	// 同步ScheduleRepository
	// 根据登录状态自动切换本地/云端数据源
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

/**
 * ViewModel模块
 * 提供ViewModel的依赖注入
 */
val exportSerializerModule = module {
    single<ScheduleStringSerializer>(named("json")) { JsonScheduleSerializer() }
    single<ScheduleStringSerializer>(named("ics")) { IcsScheduleSerializer() }
}

val reminderModule = module {
    single<ReminderScheduler> {
        ReminderManager(
            context = androidContext(),
            reminderReceiverClass = get(named("reminderReceiver"))
        )
    }
}

val viewModelModule = module {
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

/**
 * 应用所有模块的集合
 * 在Application中使用
 */
val appModules = listOf(
    databaseModule,
    repositoryModule,
    exportSerializerModule,
    reminderModule,
    viewModelModule,
    networkModule
)

