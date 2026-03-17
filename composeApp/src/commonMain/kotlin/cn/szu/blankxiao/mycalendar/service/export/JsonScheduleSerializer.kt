package cn.szu.blankxiao.mycalendar.service.export

import cn.szu.blankxiao.mycalendar.data.local.entity.ScheduleEntity
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

/**
 * JSON 格式日程序列化实现（平台无关）
 * 解析/序列化逻辑在 shared，供 PC/iOS 复用
 */
class JsonScheduleSerializer : ScheduleStringSerializer {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    @Serializable
    data class ExportData(
        val version: String = "1.0",
        val exportDate: String,
        val appName: String = "MyCalendar",
        val totalCount: Int,
        val schedules: List<ScheduleExportModel>
    )

    @Serializable
    data class ScheduleExportModel(
        val id: Long,
        val title: String,
        val date: Long,
        val description: String,
        val isChecked: Boolean,
        val createdAt: Long,
        val updatedAt: Long,
        val reminderEnabled: Boolean,
        val reminderTime: Long?
    )

    override fun exportToString(entities: List<ScheduleEntity>): String {
        val exportModels = entities.map { entity ->
            ScheduleExportModel(
                id = entity.id,
                title = entity.title,
                date = entity.date,
                description = entity.description,
                isChecked = entity.isChecked,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                reminderEnabled = entity.reminderEnabled,
                reminderTime = entity.reminderTime
            )
        }
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val exportDateStr = "${now.year}-${now.monthNumber.toString().padStart(2, '0')}-${now.dayOfMonth.toString().padStart(2, '0')} ${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}:${now.second.toString().padStart(2, '0')}"
        val exportData = ExportData(
            exportDate = exportDateStr,
            totalCount = entities.size,
            schedules = exportModels
        )
        return json.encodeToString(exportData)
    }

    override fun importFromString(content: String): Result<List<ScheduleEntity>> {
        return try {
            if (content.isBlank()) {
                return Result.failure(IllegalArgumentException("文件内容为空"))
            }
            val exportData = try {
                json.decodeFromString<ExportData>(content)
            } catch (e: SerializationException) {
                return Result.failure(IllegalArgumentException("JSON格式错误：文件不是有效的日程备份文件"))
            }
            if (exportData.appName != "MyCalendar") {
                return Result.failure(IllegalArgumentException("文件格式错误：不是 MyCalendar 的备份文件"))
            }
            exportData.schedules.forEach { model ->
                if (model.title.isBlank()) {
                    return Result.failure(IllegalArgumentException("数据格式错误：日程标题不能为空"))
                }
                if (model.date <= 0) {
                    return Result.failure(IllegalArgumentException("数据格式错误：日程日期无效"))
                }
            }
            val entities = exportData.schedules.map { model ->
                ScheduleEntity(
                    id = 0,
                    title = model.title,
                    date = model.date,
                    description = model.description,
                    isChecked = model.isChecked,
                    createdAt = model.createdAt,
                    updatedAt = Clock.System.now().toEpochMilliseconds(),
                    reminderEnabled = model.reminderEnabled,
                    reminderTime = model.reminderTime
                )
            }
            Result.success(entities)
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("导入失败：${e.message}"))
        }
    }

    override fun getFileExtension(): String = "json"
}
