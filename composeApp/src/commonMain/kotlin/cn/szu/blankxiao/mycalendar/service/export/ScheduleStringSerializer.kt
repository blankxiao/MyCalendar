package cn.szu.blankxiao.mycalendar.service.export

import cn.szu.blankxiao.mycalendar.data.local.entity.ScheduleEntity

/**
 * 日程字符串序列化接口（平台无关）
 * 用于 ViewModel 的导出/导入，不依赖 Context、File、Uri
 */
interface ScheduleStringSerializer {

    fun exportToString(entities: List<ScheduleEntity>): String

    fun importFromString(content: String): Result<List<ScheduleEntity>>

    fun getFileExtension(): String
}
