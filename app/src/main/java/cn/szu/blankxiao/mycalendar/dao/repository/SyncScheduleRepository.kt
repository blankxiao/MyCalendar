package cn.szu.blankxiao.mycalendar.dao.repository

import cn.szu.blankxiao.mycalendar.auth.TokenManager
import cn.szu.blankxiao.mycalendar.dao.local.entity.ScheduleEntity
import cn.szu.blankxiao.mycalendar.data.schedule.ScheduleItemData
import cn.szu.blankxiao.mycalendar.remote.calendar.model.SyncScheduleItem
import cn.szu.blankxiao.mycalendar.remote.calendar.model.SyncScheduleRequest
import cn.szu.blankxiao.mycalendar.remote.calendar.model.SyncScheduleResponse
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId

/**
 * 同步日程仓库
 * 根据登录状态自动切换本地/云端数据源
 * 
 * 策略：
 * - 未登录：使用本地数据
 * - 已登录：使用云端数据（后期可扩展为本地+云端同步）
 */
class SyncScheduleRepository(
    private val localRepository: ScheduleRepository,
    private val remoteRepository: RemoteScheduleRepository,
    private val tokenManager: TokenManager
) : ScheduleRepository {

    /**
     * 是否使用云端数据
     */
    private suspend fun isUsingCloud(): Boolean {
        return tokenManager.getToken() != null
    }

    // ============ 查询操作 ============

    override fun getAllSchedules(): Flow<List<ScheduleItemData>> {
        // 本地数据作为默认数据源
        // TODO: 登录后可以在ViewModel层切换到云端数据
        return localRepository.getAllSchedules()
    }

    override fun getSchedulesByDate(date: LocalDate): Flow<List<ScheduleItemData>> {
        return localRepository.getSchedulesByDate(date)
    }

    override fun getUncompletedSchedules(): Flow<List<ScheduleItemData>> {
        return localRepository.getUncompletedSchedules()
    }

    override fun getCompletedSchedules(): Flow<List<ScheduleItemData>> {
        return localRepository.getCompletedSchedules()
    }

    override suspend fun getAllScheduleEntities(): List<ScheduleEntity> {
        return localRepository.getAllScheduleEntities()
    }

    // ============ 写入操作 ============

    override suspend fun addSchedule(scheduleData: ScheduleItemData): Long {
        val localId = localRepository.addSchedule(scheduleData)
        
        // 如果已登录，同步到云端
        if (isUsingCloud()) {
            syncToCloud(scheduleData.copy(id = localId))
        }
        
        return localId
    }

    override suspend fun updateSchedule(scheduleData: ScheduleItemData) {
        localRepository.updateSchedule(scheduleData)
        
        if (isUsingCloud()) {
            syncToCloud(scheduleData)
        }
    }

    override suspend fun deleteSchedule(scheduleData: ScheduleItemData) {
        localRepository.deleteSchedule(scheduleData)
        
        if (isUsingCloud()) {
            try {
                remoteRepository.deleteSchedule(scheduleData.id)
            } catch (e: Exception) {
                // 云端删除失败，记录日志
                e.printStackTrace()
            }
        }
    }

    override suspend fun deleteAllSchedules() {
        localRepository.deleteAllSchedules()
        // 云端清空需要调用导出接口，暂不实现
    }

    override suspend fun toggleScheduleStatus(scheduleData: ScheduleItemData) {
        localRepository.toggleScheduleStatus(scheduleData)
        
        if (isUsingCloud()) {
            try {
                remoteRepository.toggleScheduleStatus(scheduleData.id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ============ 同步操作 ============

    /**
     * 同步单个日程到云端（内部使用）
     */
    private suspend fun syncToCloud(scheduleData: ScheduleItemData) {
        // 单个同步时，使用批量同步接口，传单条数据
        val syncItem = scheduleDataToSyncItem(scheduleData)
        val request = SyncScheduleRequest(
            schedules = listOf(syncItem),
            syncMode = "MERGE"
        )
        remoteRepository.syncSchedules(request)
    }

    /**
     * 完整同步：将本地所有数据同步到云端
     * @param syncMode 同步模式：MERGE（合并）、LOCAL_FIRST（本地优先）、CLOUD_FIRST（云端优先）
     * @return 同步结果
     */
    suspend fun fullSync(syncMode: String = "MERGE"): Result<SyncScheduleResponse?> {
        if (!isUsingCloud()) {
            return Result.failure(Exception("未登录"))
        }
        
        // 获取本地所有日程
        val localEntities = localRepository.getAllScheduleEntities()
        
        // 转换为同步请求格式
        val syncItems = localEntities.map { entity ->
            SyncScheduleItem(
                localId = entity.id,
                title = entity.title,
                description = entity.description,
                scheduleDate = entity.date,
                isChecked = entity.isChecked,
                reminderEnabled = entity.reminderEnabled,
                reminderTime = entity.reminderTime,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
        
        val request = SyncScheduleRequest(
            schedules = syncItems,
            syncMode = syncMode
        )
        
        // 调用同步接口
        val result = remoteRepository.syncSchedules(request)
        
        // 如果同步成功，用云端数据更新本地
        result.getOrNull()?.let { response ->
            updateLocalFromCloudResponse(response)
        }
        
        return result
    }

    /**
     * 从云端拉取数据到本地（云端优先）
     */
    suspend fun syncFromCloud(): Result<SyncScheduleResponse?> {
        return fullSync("CLOUD_FIRST")
    }

    /**
     * 将本地数据推送到云端（本地优先）
     */
    suspend fun pushToCloud(): Result<SyncScheduleResponse?> {
        return fullSync("LOCAL_FIRST")
    }

    /**
     * 智能合并同步
     */
    suspend fun mergeSync(): Result<SyncScheduleResponse?> {
        return fullSync("MERGE")
    }

    // ============ 辅助方法 ============

    /**
     * 将 ScheduleItemData 转换为 SyncScheduleItem
     */
    private fun scheduleDataToSyncItem(data: ScheduleItemData): SyncScheduleItem {
        return SyncScheduleItem(
            localId = data.id,
            title = data.title,
            description = data.description,
            scheduleDate = data.date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            isChecked = data.isChecked,
            reminderEnabled = data.reminderEnabled,
            reminderTime = data.reminderTime?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
            createdAt = data.createdAt,
            updatedAt = data.updatedAt
        )
    }

    /**
     * 根据云端响应更新本地数据
     */
    private suspend fun updateLocalFromCloudResponse(response: SyncScheduleResponse) {
        response.allSchedules?.let { cloudSchedules ->
            // 清空本地数据后导入云端数据
            localRepository.deleteAllSchedules()
            
            cloudSchedules.forEach { cloudSchedule ->
                val scheduleData = ScheduleItemData(
                    id = cloudSchedule.id ?: 0L,
                    title = cloudSchedule.title ?: "",
                    description = cloudSchedule.description ?: "",
                    date = cloudSchedule.scheduleDate?.let { 
                        java.time.Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    } ?: LocalDate.now(),
                    isChecked = cloudSchedule.isChecked ?: false,
                    reminderEnabled = cloudSchedule.reminderEnabled ?: false,
                    reminderTime = cloudSchedule.reminderTime?.let {
                        java.time.Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                    },
                    createdAt = cloudSchedule.createdAt ?: System.currentTimeMillis(),
                    updatedAt = cloudSchedule.updatedAt ?: System.currentTimeMillis()
                )
                localRepository.addSchedule(scheduleData)
            }
        }
    }
}

