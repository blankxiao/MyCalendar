package cn.szu.blankxiao.mycalendar.service.export

import android.content.Context
import android.util.Log
import cn.szu.blankxiao.mycalendar.data.local.entity.ScheduleEntity
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * @author BlankXiao
 * @description iCalendar (.ics) 格式导入导出实现
 * @date 2025-12-13
 * 
 * iCalendar 是 RFC 5545 标准格式，被 Google Calendar、Apple Calendar、Outlook 等支持
 */
class IcsExportImportManager : ExportImportManager {
    
    private val tag = "IcsExportImport"
    
    // iCalendar 日期时间格式
    private val icsDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
    private val icsDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    
    companion object {
        private const val VCALENDAR_BEGIN = "BEGIN:VCALENDAR"
        private const val VCALENDAR_END = "END:VCALENDAR"
        private const val VEVENT_BEGIN = "BEGIN:VEVENT"
        private const val VEVENT_END = "END:VEVENT"
        private const val VALARM_BEGIN = "BEGIN:VALARM"
        private const val VALARM_END = "END:VALARM"
        private const val VERSION = "VERSION:2.0"
        private const val PRODID = "PRODID:-//MyCalendar//Android//CN"
        private const val CALSCALE = "CALSCALE:GREGORIAN"
        private const val METHOD = "METHOD:PUBLISH"
    }
    
    override suspend fun exportData(
        context: Context,
        schedules: List<ScheduleEntity>,
        outputFile: File
    ): Result<File> {
        return try {
            val icsContent = buildIcsContent(schedules)
            
            // 确保目录存在
            outputFile.parentFile?.mkdirs()
            
            // 写入文件
            outputFile.writeText(icsContent)
            
            Log.d(tag, "ICS导出成功: ${outputFile.absolutePath}, 共 ${schedules.size} 条数据")
            Result.success(outputFile)
            
        } catch (e: Exception) {
            Log.e(tag, "ICS导出失败", e)
            Result.failure(e)
        }
    }
    
    override suspend fun importData(
        context: Context,
        inputFile: File
    ): Result<List<ScheduleEntity>> {
        return try {
            val icsContent = inputFile.readText()
            parseIcsContent(icsContent)
        } catch (e: Exception) {
            Log.e(tag, "ICS导入失败", e)
            Result.failure(e)
        }
    }
    
    override fun getFileExtension(): String = "ics"
    
    override fun getMimeType(): String = "text/calendar"
    
    override fun exportToString(schedules: List<ScheduleEntity>): String {
        return buildIcsContent(schedules)
    }
    
    override fun importFromString(jsonString: String): Result<List<ScheduleEntity>> {
        return try {
            if (jsonString.isBlank()) {
                return Result.failure(IllegalArgumentException("文件内容为空"))
            }
            parseIcsContent(jsonString)
        } catch (e: Exception) {
            Log.e(tag, "ICS解析失败", e)
            Result.failure(IllegalArgumentException("ICS格式错误：${e.message}"))
        }
    }
    
    /**
     * 构建 iCalendar 内容
     */
    private fun buildIcsContent(schedules: List<ScheduleEntity>): String {
        val sb = StringBuilder()
        
        // 日历头
        sb.appendLine(VCALENDAR_BEGIN)
        sb.appendLine(VERSION)
        sb.appendLine(PRODID)
        sb.appendLine(CALSCALE)
        sb.appendLine(METHOD)
        sb.appendLine("X-WR-CALNAME:MyCalendar")
        
        // 事件
        schedules.forEach { schedule ->
            sb.append(buildVEvent(schedule))
        }
        
        // 日历尾
        sb.appendLine(VCALENDAR_END)
        
        return sb.toString()
    }
    
    /**
     * 构建单个 VEVENT
     */
    private fun buildVEvent(schedule: ScheduleEntity): String {
        val sb = StringBuilder()
        
        // 将时间戳转换为 LocalDate
        val date = Instant.ofEpochMilli(schedule.date)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        
        // 事件开始时间（全天事件用 DATE 格式）
        val dtStart = date.format(icsDateFormatter)
        // 事件结束时间（全天事件，结束日期为开始日期的下一天）
        val dtEnd = date.plusDays(1).format(icsDateFormatter)
        
        // 创建时间
        val created = Instant.ofEpochMilli(schedule.createdAt)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(icsDateTimeFormatter)
        
        // 修改时间
        val lastModified = Instant.ofEpochMilli(schedule.updatedAt)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(icsDateTimeFormatter)
        
        // UID：使用 id + 域名生成唯一标识
        val uid = "${schedule.id}-${schedule.createdAt}@mycalendar.app"
        
        sb.appendLine(VEVENT_BEGIN)
        sb.appendLine("UID:$uid")
        sb.appendLine("DTSTART;VALUE=DATE:$dtStart")
        sb.appendLine("DTEND;VALUE=DATE:$dtEnd")
        sb.appendLine("SUMMARY:${escapeIcsText(schedule.title)}")
        
        if (schedule.description.isNotBlank()) {
            sb.appendLine("DESCRIPTION:${escapeIcsText(schedule.description)}")
        }
        
        sb.appendLine("CREATED:${created}Z")
        sb.appendLine("LAST-MODIFIED:${lastModified}Z")
        sb.appendLine("DTSTAMP:${lastModified}Z")
        
        // 状态：已完成用 COMPLETED，未完成用 CONFIRMED
        if (schedule.isChecked) {
            sb.appendLine("STATUS:COMPLETED")
        } else {
            sb.appendLine("STATUS:CONFIRMED")
        }
        
        // 提醒
        if (schedule.reminderEnabled && schedule.reminderTime != null) {
            sb.append(buildVAlarm(schedule))
        }
        
        sb.appendLine(VEVENT_END)
        
        return sb.toString()
    }
    
    /**
     * 构建提醒 VALARM
     */
    private fun buildVAlarm(schedule: ScheduleEntity): String {
        val sb = StringBuilder()
        
        val reminderTime = schedule.reminderTime ?: return ""
        
        val reminderDateTime = Instant.ofEpochMilli(reminderTime)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        
        sb.appendLine(VALARM_BEGIN)
        sb.appendLine("ACTION:DISPLAY")
        sb.appendLine("DESCRIPTION:${escapeIcsText(schedule.title)}")
        
        // 使用绝对时间
        val triggerTime = reminderDateTime.format(icsDateTimeFormatter)
        sb.appendLine("TRIGGER;VALUE=DATE-TIME:${triggerTime}Z")
        
        sb.appendLine(VALARM_END)
        
        return sb.toString()
    }
    
    /**
     * 转义 iCalendar 文本
     */
    private fun escapeIcsText(text: String): String {
        return text
            .replace("\\", "\\\\")
            .replace(",", "\\,")
            .replace(";", "\\;")
            .replace("\n", "\\n")
            .replace("\r", "")
    }
    
    /**
     * 解析 iCalendar 内容
     */
    private fun parseIcsContent(content: String): Result<List<ScheduleEntity>> {
        val schedules = mutableListOf<ScheduleEntity>()
        
        // 检查是否是有效的 iCalendar 文件
        if (!content.contains(VCALENDAR_BEGIN) || !content.contains(VCALENDAR_END)) {
            return Result.failure(IllegalArgumentException("不是有效的 iCalendar 文件"))
        }
        
        // 提取所有 VEVENT
        val eventPattern = Regex("BEGIN:VEVENT(.*?)END:VEVENT", RegexOption.DOT_MATCHES_ALL)
        val events = eventPattern.findAll(content)
        
        for (eventMatch in events) {
            val eventContent = eventMatch.groupValues[1]
            val schedule = parseVEvent(eventContent)
            if (schedule != null) {
                schedules.add(schedule)
            }
        }
        
        Log.d(tag, "ICS解析成功, 共 ${schedules.size} 条数据")
        return Result.success(schedules)
    }
    
    /**
     * 解析单个 VEVENT
     */
    private fun parseVEvent(content: String): ScheduleEntity? {
        val lines = content.lines().map { it.trim() }.filter { it.isNotBlank() }
        
        var title = ""
        var description = ""
        var date: Long = 0
        var isChecked = false
        var createdAt = System.currentTimeMillis()
        var reminderEnabled = false
        var reminderTime: Long? = null
        
        for (line in lines) {
            when {
                line.startsWith("SUMMARY:") -> {
                    title = unescapeIcsText(line.substringAfter("SUMMARY:"))
                }
                line.startsWith("DESCRIPTION:") -> {
                    description = unescapeIcsText(line.substringAfter("DESCRIPTION:"))
                }
                line.startsWith("DTSTART") -> {
                    date = parseDtStart(line)
                }
                line.startsWith("STATUS:COMPLETED") -> {
                    isChecked = true
                }
                line.startsWith("CREATED:") -> {
                    createdAt = parseDateTime(line.substringAfter("CREATED:")) ?: createdAt
                }
                line.startsWith("TRIGGER") -> {
                    reminderEnabled = true
                    reminderTime = parseTrigger(line, date)
                }
            }
        }
        
        // 必须有标题和日期
        if (title.isBlank() || date == 0L) {
            return null
        }
        
        return ScheduleEntity(
            id = 0, // 导入时重新生成ID
            title = title,
            date = date,
            description = description,
            isChecked = isChecked,
            createdAt = createdAt,
            updatedAt = System.currentTimeMillis(),
            reminderEnabled = reminderEnabled,
            reminderTime = reminderTime
        )
    }
    
    /**
     * 解析 DTSTART
     */
    private fun parseDtStart(line: String): Long {
        return try {
            val value = line.substringAfter(":")
            
            when {
                // 全天事件：DTSTART;VALUE=DATE:20251213
                value.length == 8 -> {
                    LocalDate.parse(value, icsDateFormatter)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                }
                // 带时间：DTSTART:20251213T090000 或 DTSTART:20251213T090000Z
                else -> {
                    val cleanValue = value.replace("Z", "")
                    LocalDateTime.parse(cleanValue.take(15), icsDateTimeFormatter)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "解析 DTSTART 失败: $line", e)
            0L
        }
    }
    
    /**
     * 解析日期时间
     */
    private fun parseDateTime(value: String): Long? {
        return try {
            val cleanValue = value.replace("Z", "").take(15)
            LocalDateTime.parse(cleanValue, icsDateTimeFormatter)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 解析提醒触发器
     */
    private fun parseTrigger(line: String, eventDate: Long): Long? {
        return try {
            when {
                // 绝对时间：TRIGGER;VALUE=DATE-TIME:20251213T083000Z
                line.contains("VALUE=DATE-TIME") -> {
                    val value = line.substringAfter(":").replace("Z", "")
                    LocalDateTime.parse(value.take(15), icsDateTimeFormatter)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                }
                // 相对时间：TRIGGER:-PT15M（提前15分钟）
                line.contains("-P") -> {
                    val duration = line.substringAfter("-P")
                    val minutes = when {
                        duration.contains("M") -> duration.replace("T", "").replace("M", "").toLongOrNull() ?: 0
                        duration.contains("H") -> (duration.replace("T", "").replace("H", "").toLongOrNull() ?: 0) * 60
                        duration.contains("D") -> (duration.replace("D", "").toLongOrNull() ?: 0) * 24 * 60
                        else -> 0
                    }
                    eventDate - minutes * 60 * 1000
                }
                else -> null
            }
        } catch (e: Exception) {
            Log.e(tag, "解析 TRIGGER 失败: $line", e)
            null
        }
    }
    
    /**
     * 反转义 iCalendar 文本
     */
    private fun unescapeIcsText(text: String): String {
        return text
            .replace("\\n", "\n")
            .replace("\\,", ",")
            .replace("\\;", ";")
            .replace("\\\\", "\\")
    }
}

