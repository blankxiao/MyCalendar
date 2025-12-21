package cn.szu.blankxiao.mycalendar.utils

import android.content.Context
import cn.szu.blankxiao.mycalendar.dao.repository.ScheduleRepository
import cn.szu.blankxiao.mycalendar.data.schedule.ScheduleItemData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * @author BlankXiao
 * @description 数据库初始化工具
 * @date 2025-12-11
 * 
 * 用于首次启动时初始化示例数据
 */
object DatabaseInitializer {
    
    private const val PREF_NAME = "app_prefs"
    private const val KEY_FIRST_LAUNCH = "is_first_launch"
    
    /**
     * 初始化数据库
     * 如果是首次启动，添加示例数据
     */
    fun initialize(context: Context, repository: ScheduleRepository) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        
        if (isFirstLaunch) {
            // 在协程中添加示例数据
            CoroutineScope(Dispatchers.IO).launch {
                // 检查数据库是否为空
                val existingSchedules = repository.getAllSchedules().first()
                
                if (existingSchedules.isEmpty()) {
                    // 添加示例数据
                    addSampleData(repository)
                }
                
                // 标记已初始化
                prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
            }
        }
    }
    
    /**
     * 添加示例数据
     */
    private suspend fun addSampleData(repository: ScheduleRepository) {
        val today = LocalDate.now()
        
        val sampleSchedules = listOf(
            ScheduleItemData(
                title = "团队周会",
                date = today,
                description = "讨论本周工作进度和下周计划",
                isChecked = false
            ),
            ScheduleItemData(
                title = "项目评审",
                date = today,
                description = "产品需求评审会议",
                isChecked = false
            ),
            ScheduleItemData(
                title = "健身运动",
                date = today,
                description = "晚上7点健身房锻炼",
                isChecked = false
            ),
            ScheduleItemData(
                title = "阅读学习",
                date = today.plusDays(1),
                description = "完成技术书籍第三章",
                isChecked = false
            ),
            ScheduleItemData(
                title = "朋友聚餐",
                date = today.plusDays(2),
                description = "周末聚餐活动",
                isChecked = false
            ),
            ScheduleItemData(
                title = "代码优化",
                date = today.minusDays(1),
                description = "重构日历组件代码",
                isChecked = true
            )
        )
        
        // 批量添加
        sampleSchedules.forEach { schedule ->
            repository.addSchedule(schedule)
        }
    }
}

