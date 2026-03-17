package cn.szu.blankxiao.mycalendar.data.repository

import cn.szu.blankxiao.mycalendar.data.local.dao.ScheduleDao
import cn.szu.blankxiao.mycalendar.data.local.entity.ScheduleEntity
import cn.szu.blankxiao.mycalendar.model.schedule.ScheduleItemData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus

/**
 * Schedule 仓库实现
 *
 * Repository 的具体实现
 * - 负责在 Entity 和 Domain Model 之间转换
 * - 协调本地数据库和远程 API（未来可扩展）
 * - 实现离线优先策略
 */
class ScheduleRepositoryImpl(
    private val scheduleDao: ScheduleDao
) : ScheduleRepositoryEx {

    override fun getAllSchedules(): Flow<List<ScheduleItemData>> {
        return scheduleDao.getAllSchedules()
            .map { entities -> entities.map { it.toScheduleItemData() } }
    }

    override fun getSchedulesByDate(date: kotlinx.datetime.LocalDate): Flow<List<ScheduleItemData>> {
        val timeZone = TimeZone.currentSystemDefault()
        val startOfDay = date.atStartOfDayIn(timeZone).toEpochMilliseconds()
        val endOfDay = date.plus(1, DateTimeUnit.DAY).atStartOfDayIn(timeZone).toEpochMilliseconds()

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
        val entity = ScheduleEntity.fromScheduleItemData(scheduleData).copy(
            updatedAt = Clock.System.now().toEpochMilliseconds()
        )
        scheduleDao.update(entity)
    }

    override suspend fun deleteSchedule(scheduleData: ScheduleItemData) {
        val entity = ScheduleEntity.fromScheduleItemData(scheduleData)
        scheduleDao.delete(entity)
    }

    override suspend fun toggleScheduleStatus(scheduleData: ScheduleItemData) {
        scheduleDao.updateCheckStatus(
            id = scheduleData.id,
            isChecked = !scheduleData.isChecked,
            updatedAt = Clock.System.now().toEpochMilliseconds()
        )
    }

    override suspend fun deleteAllSchedules() {
        scheduleDao.deleteAll()
    }

    override suspend fun getAllScheduleEntities(): List<ScheduleEntity> {
        return scheduleDao.getAllSchedulesOnce()
    }
}
