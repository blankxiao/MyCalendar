package cn.szu.blankxiao.mycalendar

import cn.szu.blankxiao.mycalendar.di.iosModules
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.logger.PrintLogger

/**
 * 初始化 Koin
 * 必须在 App 启动时、任何 Compose UI 之前调用
 * 在 Swift AppDelegate 的 application:didFinishLaunchingWithOptions 中调用
 */
fun initKoin() {
    startKoin {
        logger(PrintLogger(Level.ERROR))
        modules(iosModules)
    }
}
