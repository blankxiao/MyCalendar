package cn.szu.blankxiao.mycalendar.data.repository

import cn.szu.blankxiao.mycalendar.remote.calendar.model.CreateScheduleRequest
import cn.szu.blankxiao.mycalendar.remote.calendar.model.QueryScheduleRequest
import cn.szu.blankxiao.mycalendar.remote.calendar.model.ScheduleResponse
import cn.szu.blankxiao.mycalendar.remote.calendar.model.SyncScheduleRequest
import cn.szu.blankxiao.mycalendar.remote.calendar.model.SyncScheduleResponse
import cn.szu.blankxiao.mycalendar.remote.calendar.model.UpdateScheduleRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import cn.szu.blankxiao.mycalendar.remote.calendar.api.DefaultApi as ScheduleApi

/**
 * 远程日程仓库实现
 * 通过OpenAPI生成的接口与后端交互
 */
class RemoteScheduleRepository(
    private val api: ScheduleApi
) {
    
    /**
     * 创建日程
     */
    suspend fun createSchedule(request: CreateScheduleRequest): Result<Long?> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.createSchedule(request)
                if (response.isSuccessful) {
                    val result = response.body()
                    // 后端的code是Int类型，成功码是0
                    if (result?.success == true) {
                        Result.success(result.data)
                    } else {
                        Result.failure(Exception(result?.info ?: "创建失败"))
                    }
                } else {
                    Result.failure(Exception("网络请求失败: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * 查询日程列表
     */
    suspend fun querySchedules(request: QueryScheduleRequest): Result<List<ScheduleResponse>?> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.querySchedules(request)
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.success == true) {
                        Result.success(result.data)
                    } else {
                        Result.failure(Exception(result?.info ?: "查询失败"))
                    }
                } else {
                    Result.failure(Exception("网络请求失败: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * 查询单个日程详情
     */
    suspend fun queryScheduleDetail(scheduleId: Long): Result<ScheduleResponse?> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.queryScheduleDetail(scheduleId)
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.success == true) {
                        Result.success(result.data)
                    } else {
                        Result.failure(Exception(result?.info ?: "查询失败"))
                    }
                } else {
                    Result.failure(Exception("网络请求失败: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * 更新日程
     */
    suspend fun updateSchedule(request: UpdateScheduleRequest): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.updateSchedule(request)
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.success == true) {
                        Result.success(Unit)
                    } else {
                        Result.failure(Exception(result?.info ?: "更新失败"))
                    }
                } else {
                    Result.failure(Exception("网络请求失败: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * 删除日程
     */
    suspend fun deleteSchedule(scheduleId: Long): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.deleteSchedule(scheduleId)
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.success == true) {
                        Result.success(Unit)
                    } else {
                        Result.failure(Exception(result?.info ?: "删除失败"))
                    }
                } else {
                    Result.failure(Exception("网络请求失败: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * 切换日程完成状态
     */
    suspend fun toggleScheduleStatus(scheduleId: Long): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.toggleScheduleStatus(scheduleId)
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.success == true) {
                        Result.success(Unit)
                    } else {
                        Result.failure(Exception(result?.info ?: "切换状态失败"))
                    }
                } else {
                    Result.failure(Exception("网络请求失败: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * 导出日程数据
     * 注意：后端通过sa-token获取userId，无需传参
     */
    suspend fun exportSchedules(): Result<List<ScheduleResponse>?> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.exportSchedules()
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.success == true) {
                        Result.success(result.data)
                    } else {
                        Result.failure(Exception(result?.info ?: "导出失败"))
                    }
                } else {
                    Result.failure(Exception("网络请求失败: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 批量同步日程到云端
     * @param request 同步请求（包含本地日程列表和同步模式）
     * @return 同步结果（包含ID映射和同步后的所有日程）
     */
    suspend fun syncSchedules(request: SyncScheduleRequest): Result<SyncScheduleResponse?> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.syncSchedules(request)
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.success == true) {
                        Result.success(result.data)
                    } else {
                        Result.failure(Exception(result?.info ?: "同步失败"))
                    }
                } else {
                    Result.failure(Exception("网络请求失败: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

