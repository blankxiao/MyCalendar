package cn.szu.blankxiao.mycalendar

import android.app.Application
import cn.szu.blankxiao.mycalendar.data.repository.ScheduleRepositoryEx
import cn.szu.blankxiao.mycalendar.di.androidConfigModule
import cn.szu.blankxiao.mycalendar.di.appModules
import cn.szu.blankxiao.mycalendar.service.reminder.NotificationHelper
import cn.szu.blankxiao.mycalendar.util.DatabaseInitializer
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * @author BlankXiao
 * @description 应用Application类
 * @date 2025-12-11
 * 
 * 应用启动入口
 * - 初始化Koin依赖注入框架
 * - 配置全局应用级别的组件
 */
class MyCalendarApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化Koin（androidConfigModule 需在 networkModule 之前加载）
        val koinApp = startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MyCalendarApplication)
            modules(androidConfigModule + appModules)
        }
        
        // 初始化数据库
        val repository = koinApp.koin.get<ScheduleRepositoryEx>()
        DatabaseInitializer.initialize(this, repository)
        
        // 创建通知渠道
        NotificationHelper.createNotificationChannel(this)
    }
}
