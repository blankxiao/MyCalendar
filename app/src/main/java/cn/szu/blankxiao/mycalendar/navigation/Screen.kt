package cn.szu.blankxiao.mycalendar.navigation

/**
 * @author BlankXiao
 * @description 应用路由定义
 * @date 2025-12-11
 */
sealed class Screen(val route: String) {
    /**
     * 主页面 - 日历和日程
     */
    data object Main : Screen("main")
    
    /**
     * 设置页面
     */
    data object Settings : Screen("settings")
    
    /**
     * 数据管理页面 - 导入导出
     */
    data object DataManagement : Screen("data_management")
    
    /**
     * 日视图页面 - 显示单日详情
     */
    data object DayView : Screen("day_view/{date}") {
        fun createRoute(date: String) = "day_view/$date"
    }
}

