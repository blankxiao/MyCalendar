package cn.szu.blankxiao.mycalendar.remote.calendar

import cn.szu.blankxiao.mycalendar.remote.calendar.model.CreateScheduleRequest
import cn.szu.blankxiao.mycalendar.remote.calendar.model.QueryScheduleRequest
import cn.szu.blankxiao.mycalendar.remote.calendar.model.ResultListScheduleResponse
import cn.szu.blankxiao.mycalendar.remote.calendar.model.ResultLong
import cn.szu.blankxiao.mycalendar.remote.calendar.model.ResultScheduleResponse
import cn.szu.blankxiao.mycalendar.remote.calendar.model.ResultSyncScheduleResponse
import cn.szu.blankxiao.mycalendar.remote.calendar.model.ResultVoid
import cn.szu.blankxiao.mycalendar.remote.calendar.model.SyncScheduleRequest
import cn.szu.blankxiao.mycalendar.remote.calendar.model.UpdateScheduleRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Calendar API 客户端（Ktor 实现）
 * Token 由 HttpClient 的拦截器统一添加
 */
class ScheduleApiClient(
    private val baseUrl: String,
    private val httpClient: HttpClient
) {

    suspend fun createSchedule(request: CreateScheduleRequest): ResultLong {
        return httpClient.post("$baseUrl/calendar/schedule/create") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun deleteSchedule(scheduleId: Long): ResultVoid {
        return httpClient.post("$baseUrl/calendar/schedule/delete?scheduleId=$scheduleId") {
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun exportSchedules(): ResultListScheduleResponse {
        return httpClient.post("$baseUrl/calendar/schedule/export") {
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun queryScheduleDetail(scheduleId: Long): ResultScheduleResponse {
        return httpClient.get("$baseUrl/calendar/schedule/detail?scheduleId=$scheduleId") {
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun querySchedules(request: QueryScheduleRequest): ResultListScheduleResponse {
        return httpClient.post("$baseUrl/calendar/schedule/query") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun syncSchedules(request: SyncScheduleRequest): ResultSyncScheduleResponse {
        return httpClient.post("$baseUrl/calendar/schedule/sync") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun toggleScheduleStatus(scheduleId: Long): ResultVoid {
        return httpClient.post("$baseUrl/calendar/schedule/toggle?scheduleId=$scheduleId") {
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun updateSchedule(request: UpdateScheduleRequest): ResultVoid {
        return httpClient.post("$baseUrl/calendar/schedule/update") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
