package cn.szu.blankxiao.mycalendar.di

import cn.szu.blankxiao.mycalendar.dao.local.AppDatabase
import cn.szu.blankxiao.mycalendar.dao.repository.ScheduleRepository
import cn.szu.blankxiao.mycalendar.dao.repository.ScheduleRepositoryImpl
import cn.szu.blankxiao.mycalendar.viewmodel.ScheduleViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
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
        AppDatabase.getInstance(androidContext())
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
    // 单例：ScheduleRepository
    single<ScheduleRepository> {
        ScheduleRepositoryImpl(
            scheduleDao = get()
        )
    }
}

/**
 * ViewModel模块
 * 提供ViewModel的依赖注入
 */
val viewModelModule = module {
    // ViewModel：ScheduleViewModel
    viewModel {
        ScheduleViewModel(
            repository = get()
        )
    }
}

/**
 * 应用所有模块的集合
 * 在Application中使用
 */
val appModules = listOf(
    databaseModule,
    repositoryModule,
    viewModelModule
)

