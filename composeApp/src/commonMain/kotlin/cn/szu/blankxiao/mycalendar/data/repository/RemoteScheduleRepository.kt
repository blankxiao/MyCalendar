package cn.szu.blankxiao.mycalendar.data.repository

import cn.szu.blankxiao.mycalendar.remote.calendar.ScheduleApiClient
import cn.szu.blankxiao.mycalendar.remote.calendar.model.CreateScheduleRequest
import cn.szu.blankxiao.mycalendar.remote.calendar.model.QueryScheduleRequest
import cn.szu.blankxiao.mycalendar.remote.calendar.model.ScheduleResponse
import cn.szu.blankxiao.mycalendar.remote.calendar.model.SyncScheduleRequest
import cn.szu.blankxiao.mycalendar.remote.calendar.model.SyncScheduleResponse
import cn.szu.blankxiao.mycalendar.remote.calendar.model.UpdateScheduleRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 远程日程仓库实现
 * 通过 Ktor 实现的 API 客户端与后端交互
 */
class RemoteScheduleRepository(
    private val api: ScheduleApiClient
) {

    suspend fun createSchedule(request: CreateScheduleRequest): Result<Long?> {
        return withContext(Dispatchers.Default) {
            try {
                val result = api.createSchedule(request)
                if (result.success == true) {
                    Result.success(result.data)
                } else {
                    Result.failure(Exception(result.info ?: "创建失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun querySchedules(request: QueryScheduleRequest): Result<List<ScheduleResponse>?> {
        return withContext(Dispatchers.Default) {
            try {
                val result = api.querySchedules(request)
                if (result.success == true) {
                    Result.success(result.data)
                } else {
                    Result.failure(Exception(result.info ?: "查询失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun queryScheduleDetail(scheduleId: Long): Result<ScheduleResponse?> {
        return withContext(Dispatchers.Default) {
            try {
                val result = api.queryScheduleDetail(scheduleId)
                if (result.success == true) {
                    Result.success(result.data)
                } else {
                    Result.failure(Exception(result.info ?: "查询失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateSchedule(request: UpdateScheduleRequest): Result<Unit> {
        return withContext(Dispatchers.Default) {
            try {
                val result = api.updateSchedule(request)
                if (result.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(result.info ?: "更新失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteSchedule(scheduleId: Long): Result<Unit> {
        return withContext(Dispatchers.Default) {
            try {
                val result = api.deleteSchedule(scheduleId)
                if (result.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(result.info ?: "删除失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun toggleScheduleStatus(scheduleId: Long): Result<Unit> {
        return withContext(Dispatchers.Default) {
            try {
                val result = api.toggleScheduleStatus(scheduleId)
                if (result.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(result.info ?: "切换状态失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun exportSchedules(): Result<List<ScheduleResponse>?> {
        return withContext(Dispatchers.Default) {
            try {
                val result = api.exportSchedules()
                if (result.success == true) {
                    Result.success(result.data)
                } else {
                    Result.failure(Exception(result.info ?: "导出失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun syncSchedules(request: SyncScheduleRequest): Result<SyncScheduleResponse?> {
        return withContext(Dispatchers.Default) {
            try {
                val result = api.syncSchedules(request)
                if (result.success == true) {
                    Result.success(result.data)
                } else {
                    Result.failure(Exception(result.info ?: "同步失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
