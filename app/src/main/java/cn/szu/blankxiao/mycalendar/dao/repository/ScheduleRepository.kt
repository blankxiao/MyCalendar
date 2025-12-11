package cn.szu.blankxiao.mycalendar.dao.repository

import cn.szu.blankxiao.mycalendar.data.schedule.ScheduleItemData
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * @author BlankXiao
 * @description Schedule仓库接口
 * @date 2025-12-11
 * 
 * Repository模式的核心接口
 * - 隐藏数据来源细节（本地数据库/远程API）
 * - 为ViewModel提供统一的数据访问接口
 * - 便于单元测试（可以Mock）
 */
interface ScheduleRepository {
    
    /**
     * 获取所有日程
     */
    fun getAllSchedules(): Flow<List<ScheduleItemData>>
    
    /**
     * 根据日期查询日程
     * @param date 指定日期
     */
    fun getSchedulesByDate(date: LocalDate): Flow<List<ScheduleItemData>>
    
    /**
     * 查询未完成的日程
     */
    fun getUncompletedSchedules(): Flow<List<ScheduleItemData>>
    
    /**
     * 查询已完成的日程
     */
    fun getCompletedSchedules(): Flow<List<ScheduleItemData>>
    
    /**
     * 添加日程
     * @param scheduleData 日程数据
     * @return 插入的记录ID
     */
    suspend fun addSchedule(scheduleData: ScheduleItemData): Long
    
    /**
     * 更新日程
     * @param scheduleData 日程数据
     */
    suspend fun updateSchedule(scheduleData: ScheduleItemData)
    
    /**
     * 删除日程
     * @param scheduleData 日程数据
     */
    suspend fun deleteSchedule(scheduleData: ScheduleItemData)
    
    /**
     * 切换日程完成状态
     * @param scheduleData 日程数据
     */
    suspend fun toggleScheduleStatus(scheduleData: ScheduleItemData)
    
    /**
     * 删除所有日程
     */
    suspend fun deleteAllSchedules()
}

