package cn.szu.blankxiao.mycalendar.data.repository

import cn.szu.blankxiao.mycalendar.data.local.entity.ScheduleEntity

/**
 * 扩展的 ScheduleRepository
 * 在共享接口基础上增加导出所需的 getAllScheduleEntities
 */
interface ScheduleRepositoryEx : ScheduleRepository {

    suspend fun getAllScheduleEntities(): List<ScheduleEntity>
}
