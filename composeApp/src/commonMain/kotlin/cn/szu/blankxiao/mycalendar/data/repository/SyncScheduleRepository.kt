package cn.szu.blankxiao.mycalendar.data.repository

import cn.szu.blankxiao.mycalendar.data.local.datastore.TokenStorage
import cn.szu.blankxiao.mycalendar.data.local.entity.ScheduleEntity
import cn.szu.blankxiao.mycalendar.model.schedule.ScheduleItemData
import cn.szu.blankxiao.mycalendar.remote.calendar.model.SyncScheduleItem
import cn.szu.blankxiao.mycalendar.remote.calendar.model.SyncScheduleRequest
import cn.szu.blankxiao.mycalendar.remote.calendar.model.SyncScheduleResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/**
 * 同步日程仓库
 * 根据登录状态自动切换本地/云端数据源
 *
 * 策略：
 * - 未登录：使用本地数据
 * - 已登录：使用云端数据（后期可扩展为本地+云端同步）
 */
class SyncScheduleRepository(
    private val localRepository: ScheduleRepositoryEx,
    private val remoteRepository: RemoteScheduleRepository,
    private val tokenStorage: TokenStorage
) : ScheduleRepositoryEx {

    private suspend fun isUsingCloud(): Boolean {
        return tokenStorage.getToken() != null
    }

    // ============ 查询操作 ============

    override fun getAllSchedules(): Flow<List<ScheduleItemData>> {
        return localRepository.getAllSchedules()
    }

    override fun getSchedulesByDate(date: kotlinx.datetime.LocalDate): Flow<List<ScheduleItemData>> {
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
                e.printStackTrace()
            }
        }
    }

    override suspend fun deleteAllSchedules() {
        localRepository.deleteAllSchedules()
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

    private suspend fun syncToCloud(scheduleData: ScheduleItemData) {
        val syncItem = scheduleDataToSyncItem(scheduleData)
        val request = SyncScheduleRequest(
            schedules = listOf(syncItem),
            syncMode = "MERGE"
        )
        remoteRepository.syncSchedules(request)
    }

    /**
     * 完整同步：将本地所有数据同步到云端
     */
    suspend fun fullSync(syncMode: String = "MERGE"): Result<SyncScheduleResponse?> {
        if (!isUsingCloud()) {
            return Result.failure(Exception("未登录"))
        }

        val localEntities = localRepository.getAllScheduleEntities()

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

        val result = remoteRepository.syncSchedules(request)

        result.getOrNull()?.let { response ->
            updateLocalFromCloudResponse(response)
        }

        return result
    }

    suspend fun syncFromCloud(): Result<SyncScheduleResponse?> {
        return fullSync("CLOUD_FIRST")
    }

    suspend fun pushToCloud(): Result<SyncScheduleResponse?> {
        return fullSync("LOCAL_FIRST")
    }

    suspend fun mergeSync(): Result<SyncScheduleResponse?> {
        return fullSync("MERGE")
    }

    // ============ 辅助方法 ============

    private fun scheduleDataToSyncItem(data: ScheduleItemData): SyncScheduleItem {
        val timeZone = TimeZone.currentSystemDefault()
        return SyncScheduleItem(
            localId = data.id,
            title = data.title,
            description = data.description,
            scheduleDate = data.date.atStartOfDayIn(timeZone).toEpochMilliseconds(),
            isChecked = data.isChecked,
            reminderEnabled = data.reminderEnabled,
            reminderTime = data.reminderTime?.toInstant(timeZone)?.toEpochMilliseconds(),
            createdAt = data.createdAt,
            updatedAt = data.updatedAt
        )
    }

    private suspend fun updateLocalFromCloudResponse(response: SyncScheduleResponse) {
        response.allSchedules?.let { cloudSchedules ->
            localRepository.deleteAllSchedules()

            cloudSchedules.forEach { cloudSchedule ->
                val timeZone = TimeZone.currentSystemDefault()
                val scheduleData = ScheduleItemData(
                    id = cloudSchedule.id ?: 0L,
                    title = cloudSchedule.title ?: "",
                    description = cloudSchedule.description ?: "",
                    date = cloudSchedule.scheduleDate?.let {
                        Instant.fromEpochMilliseconds(it)
                            .toLocalDateTime(timeZone)
                            .date
                    } ?: Clock.System.now().toLocalDateTime(timeZone).date,
                    isChecked = cloudSchedule.isChecked ?: false,
                    reminderEnabled = cloudSchedule.reminderEnabled ?: false,
                    reminderTime = cloudSchedule.reminderTime?.let {
                        Instant.fromEpochMilliseconds(it).toLocalDateTime(timeZone)
                    },
                    createdAt = cloudSchedule.createdAt ?: Clock.System.now().toEpochMilliseconds(),
                    updatedAt = cloudSchedule.updatedAt ?: Clock.System.now().toEpochMilliseconds()
                )
                localRepository.addSchedule(scheduleData)
            }
        }
    }
}
