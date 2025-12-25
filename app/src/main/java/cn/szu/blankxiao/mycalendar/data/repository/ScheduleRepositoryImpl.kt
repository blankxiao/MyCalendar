package cn.szu.blankxiao.mycalendar.data.repository

import cn.szu.blankxiao.mycalendar.data.local.dao.ScheduleDao
import cn.szu.blankxiao.mycalendar.data.local.entity.ScheduleEntity
import cn.szu.blankxiao.mycalendar.model.schedule.ScheduleItemData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId

/**
 * @author BlankXiao
 * @description Schedule仓库实现
 * @date 2025-12-11
 * 
 * Repository的具体实现
 * - 负责在Entity和Domain Model之间转换
 * - 协调本地数据库和远程API（未来可扩展）
 * - 实现离线优先策略
 */
class ScheduleRepositoryImpl(
    private val scheduleDao: ScheduleDao
) : ScheduleRepository {
    
    override fun getAllSchedules(): Flow<List<ScheduleItemData>> {
        return scheduleDao.getAllSchedules()
            .map { entities -> entities.map { it.toScheduleItemData() } }
    }
    
    override fun getSchedulesByDate(date: LocalDate): Flow<List<ScheduleItemData>> {
        // 将LocalDate转换为时间戳范围（当天00:00:00 到 23:59:59）
        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        return scheduleDao.getSchedulesByDateRange(startOfDay, endOfDay)
            .map { entities -> entities.map { it.toScheduleItemData() } }
    }
    
    override fun getUncompletedSchedules(): Flow<List<ScheduleItemData>> {
        return scheduleDao.getUncompletedSchedules()
            .map { entities -> entities.map { it.toScheduleItemData() } }
    }
    
    override fun getCompletedSchedules(): Flow<List<ScheduleItemData>> {
        return scheduleDao.getCompletedSchedules()
            .map { entities -> entities.map { it.toScheduleItemData() } }
    }
    
    override suspend fun addSchedule(scheduleData: ScheduleItemData): Long {
        val entity = ScheduleEntity.fromScheduleItemData(scheduleData)
        return scheduleDao.insert(entity)
    }
    
    override suspend fun updateSchedule(scheduleData: ScheduleItemData) {
        // 更新时刷新 updatedAt 时间戳，确保重排序
        val entity = ScheduleEntity.fromScheduleItemData(scheduleData).copy(
            updatedAt = System.currentTimeMillis()
        )
        scheduleDao.update(entity)
    }
    
    override suspend fun deleteSchedule(scheduleData: ScheduleItemData) {
        val entity = ScheduleEntity.fromScheduleItemData(scheduleData)
        scheduleDao.delete(entity)
    }
    
    override suspend fun toggleScheduleStatus(scheduleData: ScheduleItemData) {
        // 使用专门的更新状态方法，自动更新 updatedAt
        scheduleDao.updateCheckStatus(
            id = scheduleData.id,
            isChecked = !scheduleData.isChecked,
            updatedAt = System.currentTimeMillis()
        )
    }
    
    override suspend fun deleteAllSchedules() {
        scheduleDao.deleteAll()
    }
    
    override suspend fun getAllScheduleEntities(): List<ScheduleEntity> {
        // 由于getAllSchedules返回Flow，我们需要添加一个新的DAO方法
        // 或者使用first()来获取当前值
        return scheduleDao.getAllSchedulesOnce()
    }
}

