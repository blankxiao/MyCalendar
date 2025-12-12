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
    
    // 未来可以添加更多页面
    // data object About : Screen("about")
    // data object ScheduleDetail : Screen("schedule_detail/{scheduleId}") {
    //     fun createRoute(scheduleId: Long) = "schedule_detail/$scheduleId"
    // }
}

