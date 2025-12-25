package cn.szu.blankxiao.mycalendar

import android.app.Application
import cn.szu.blankxiao.mycalendar.data.repository.ScheduleRepository
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
        
        // 初始化Koin
        val koinApp = startKoin {
            // 日志级别（开发环境使用ERROR，生产环境使用NONE）
            androidLogger(Level.ERROR)
            
            // 传入Android Context
            androidContext(this@MyCalendarApplication)
            
            // 加载所有模块
            modules(appModules)
        }
        
        // 初始化数据库
        val repository = koinApp.koin.get<ScheduleRepository>()
        DatabaseInitializer.initialize(this, repository)
        
        // 创建通知渠道
        NotificationHelper.createNotificationChannel(this)
    }
}

