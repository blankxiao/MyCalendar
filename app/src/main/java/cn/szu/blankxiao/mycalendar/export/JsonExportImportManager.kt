package cn.szu.blankxiao.mycalendar.export

import android.content.Context
import android.util.Log
import cn.szu.blankxiao.mycalendar.dao.local.entity.ScheduleEntity
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author BlankXiao
 * @description JSON格式导入导出实现
 * @date 2025-12-11
 */
class JsonExportImportManager : ExportImportManager {
    
    private val tag = "JsonExportImport"
    
    // 配置JSON序列化器（美化输出）
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }
    
    /**
     * 导出数据模型（用于JSON序列化）
     */
    @Serializable
    data class ExportData(
        val version: String = "1.0",
        val exportDate: String,
        val appName: String = "MyCalendar",
        val totalCount: Int,
        val schedules: List<ScheduleExportModel>
    )
    
    /**
     * 日程导出模型
     */
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
    
    override suspend fun exportData(
        context: Context,
        schedules: List<ScheduleEntity>,
        outputFile: File
    ): Result<File> {
        return try {
            // 转换为导出模型
            val exportModels = schedules.map { entity ->
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
            
            // 创建导出数据
            val exportData = ExportData(
                exportDate = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                ),
                totalCount = schedules.size,
                schedules = exportModels
            )
            
            // 序列化为JSON
            val jsonString = json.encodeToString(exportData)
            
            // 确保目录存在
            outputFile.parentFile?.mkdirs()
            
            // 写入文件
            outputFile.writeText(jsonString)
            
            Log.d(tag, "导出成功: ${outputFile.absolutePath}, 共 ${schedules.size} 条数据")
            Result.success(outputFile)
            
        } catch (e: Exception) {
            Log.e(tag, "导出失败", e)
            Result.failure(e)
        }
    }
    
    override suspend fun importData(
        context: Context,
        inputFile: File
    ): Result<List<ScheduleEntity>> {
        return try {
            // 读取文件内容
            val jsonString = inputFile.readText()
            
            // 反序列化
            val exportData = json.decodeFromString<ExportData>(jsonString)
            
            // 转换为Entity
            val entities: List<ScheduleEntity> = exportData.schedules.map { model ->
                ScheduleEntity(
                    id = 0, // 导入时重新生成ID
                    title = model.title,
                    date = model.date,
                    description = model.description,
                    isChecked = model.isChecked,
                    createdAt = model.createdAt,
                    updatedAt = System.currentTimeMillis(), // 更新时间戳
                    reminderEnabled = model.reminderEnabled,
                    reminderTime = model.reminderTime
                )
            }
            
            Log.d(tag, "导入成功: ${inputFile.absolutePath}, 共 ${entities.size} 条数据")
            Result.success(entities)
            
        } catch (e: Exception) {
            Log.e(tag, "导入失败", e)
            Result.failure(e)
        }
    }
    
    override fun getFileExtension(): String = "json"
    
    override fun getMimeType(): String = "application/json"
    
    /**
     * 导出数据为JSON字符串
     */
    override fun exportToString(schedules: List<ScheduleEntity>): String {
        // 转换为导出模型
        val exportModels = schedules.map { entity ->
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
        
        // 创建导出数据
        val exportData = ExportData(
            exportDate = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            ),
            totalCount = schedules.size,
            schedules = exportModels
        )
        
        // 序列化为JSON
        return json.encodeToString(exportData)
    }
    
    /**
     * 从JSON字符串导入数据
     */
    override fun importFromString(jsonString: String): Result<List<ScheduleEntity>> {
        return try {
            // 检查是否为空
            if (jsonString.isBlank()) {
                return Result.failure(IllegalArgumentException("文件内容为空"))
            }
            
            // 尝试解析JSON
            val exportData: ExportData
            try {
                exportData = json.decodeFromString<ExportData>(jsonString)
            } catch (e: SerializationException) {
                Log.e(tag, "JSON格式错误", e)
                return Result.failure(IllegalArgumentException("JSON格式错误：文件不是有效的日程备份文件"))
            }
            
            // 验证数据格式
            if (exportData.appName != "MyCalendar") {
                return Result.failure(IllegalArgumentException("文件格式错误：不是 MyCalendar 的备份文件"))
            }
            
            // 验证日程数据
            exportData.schedules.forEach { model ->
                if (model.title.isBlank()) {
                    return Result.failure(IllegalArgumentException("数据格式错误：日程标题不能为空"))
                }
                if (model.date <= 0) {
                    return Result.failure(IllegalArgumentException("数据格式错误：日程日期无效"))
                }
            }
            
            // 转换为Entity
            val entities = exportData.schedules.map { model ->
                ScheduleEntity(
                    id = 0, // 导入时重新生成ID
                    title = model.title,
                    date = model.date,
                    description = model.description,
                    isChecked = model.isChecked,
                    createdAt = model.createdAt,
                    updatedAt = System.currentTimeMillis(),
                    reminderEnabled = model.reminderEnabled,
                    reminderTime = model.reminderTime
                )
            }
            
            Log.d(tag, "从字符串导入成功, 共 ${entities.size} 条数据")
            Result.success(entities)
            
        } catch (e: Exception) {
            Log.e(tag, "导入失败", e)
            Result.failure(IllegalArgumentException("导入失败：${e.message}"))
        }
    }
}

