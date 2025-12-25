package cn.szu.blankxiao.mycalendar.remote.calendar.api

import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import cn.szu.blankxiao.mycalendar.remote.calendar.model.CreateScheduleRequest
import cn.szu.blankxiao.mycalendar.remote.calendar.model.QueryScheduleRequest
import cn.szu.blankxiao.mycalendar.remote.calendar.model.ResultListScheduleResponse
import cn.szu.blankxiao.mycalendar.remote.calendar.model.ResultLong
import cn.szu.blankxiao.mycalendar.remote.calendar.model.ResultScheduleResponse
import cn.szu.blankxiao.mycalendar.remote.calendar.model.ResultSyncScheduleResponse
import cn.szu.blankxiao.mycalendar.remote.calendar.model.ResultVoid
import cn.szu.blankxiao.mycalendar.remote.calendar.model.SyncScheduleRequest
import cn.szu.blankxiao.mycalendar.remote.calendar.model.UpdateScheduleRequest

interface DefaultApi {
    /**
     * 创建日程
     * 创建一个新的日程安排
     * Responses:
     *  - 200: OK
     *
     * @param createScheduleRequest 
     * @return [ResultLong]
     */
    @POST("calendar/schedule/create")
    suspend fun createSchedule(@Body createScheduleRequest: CreateScheduleRequest): Response<ResultLong>

    /**
     * 删除日程
     * 根据ID删除指定日程
     * Responses:
     *  - 200: OK
     *
     * @param scheduleId 日程ID
     * @return [ResultVoid]
     */
    @POST("calendar/schedule/delete")
    suspend fun deleteSchedule(@Query("scheduleId") scheduleId: kotlin.Long): Response<ResultVoid>

    /**
     * 导出日程
     * 导出当前用户的所有日程数据
     * Responses:
     *  - 200: OK
     *
     * @return [ResultListScheduleResponse]
     */
    @POST("calendar/schedule/export")
    suspend fun exportSchedules(): Response<ResultListScheduleResponse>

    /**
     * 查询日程详情
     * 根据ID查询日程详细信息
     * Responses:
     *  - 200: OK
     *
     * @param scheduleId 日程ID
     * @return [ResultScheduleResponse]
     */
    @GET("calendar/schedule/detail")
    suspend fun queryScheduleDetail(@Query("scheduleId") scheduleId: kotlin.Long): Response<ResultScheduleResponse>

    /**
     * 查询日程列表
     * 根据条件查询日程列表
     * Responses:
     *  - 200: OK
     *
     * @param queryScheduleRequest 
     * @return [ResultListScheduleResponse]
     */
    @POST("calendar/schedule/query")
    suspend fun querySchedules(@Body queryScheduleRequest: QueryScheduleRequest): Response<ResultListScheduleResponse>

    /**
     * 批量同步日程
     * 将本地日程批量同步到云端，支持MERGE(合并)/LOCAL_FIRST(本地优先)/CLOUD_FIRST(云端优先)三种模式
     * Responses:
     *  - 200: OK
     *
     * @param syncScheduleRequest 
     * @return [ResultSyncScheduleResponse]
     */
    @POST("calendar/schedule/sync")
    suspend fun syncSchedules(@Body syncScheduleRequest: SyncScheduleRequest): Response<ResultSyncScheduleResponse>

    /**
     * 切换日程状态
     * 切换日程的完成/未完成状态
     * Responses:
     *  - 200: OK
     *
     * @param scheduleId 日程ID
     * @return [ResultVoid]
     */
    @POST("calendar/schedule/toggle")
    suspend fun toggleScheduleStatus(@Query("scheduleId") scheduleId: kotlin.Long): Response<ResultVoid>

    /**
     * 更新日程
     * 更新现有日程的信息
     * Responses:
     *  - 200: OK
     *
     * @param updateScheduleRequest 
     * @return [ResultVoid]
     */
    @POST("calendar/schedule/update")
    suspend fun updateSchedule(@Body updateScheduleRequest: UpdateScheduleRequest): Response<ResultVoid>

}
