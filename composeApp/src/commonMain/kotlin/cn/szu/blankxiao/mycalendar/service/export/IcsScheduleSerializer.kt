package cn.szu.blankxiao.mycalendar.service.export

import cn.szu.blankxiao.mycalendar.data.local.entity.ScheduleEntity
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toLocalDateTime

/**
 * iCalendar (.ics) 格式日程序列化实现（平台无关）
 * RFC 5545 标准，被 Google Calendar、Apple Calendar、Outlook 等支持
 */
class IcsScheduleSerializer : ScheduleStringSerializer {

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

    private val timeZone = TimeZone.currentSystemDefault()

    override fun exportToString(entities: List<ScheduleEntity>): String = buildIcsContent(entities)

    override fun importFromString(content: String): Result<List<ScheduleEntity>> {
        return try {
            if (content.isBlank()) {
                return Result.failure(IllegalArgumentException("文件内容为空"))
            }
            parseIcsContent(content)
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("ICS格式错误：${e.message}"))
        }
    }

    override fun getFileExtension(): String = "ics"

    private fun buildIcsContent(schedules: List<ScheduleEntity>): String {
        val sb = StringBuilder()
        sb.appendLine(VCALENDAR_BEGIN)
        sb.appendLine(VERSION)
        sb.appendLine(PRODID)
        sb.appendLine(CALSCALE)
        sb.appendLine(METHOD)
        sb.appendLine("X-WR-CALNAME:MyCalendar")
        schedules.forEach { sb.append(buildVEvent(it)) }
        sb.appendLine(VCALENDAR_END)
        return sb.toString()
    }

    private fun buildVEvent(schedule: ScheduleEntity): String {
        val sb = StringBuilder()
        val date = Instant.fromEpochMilliseconds(schedule.date)
            .toLocalDateTime(timeZone).date
        val dtStart = formatIcsDate(date)
        val dtEnd = formatIcsDate(date.plus(1, DateTimeUnit.DAY))
        val created = formatIcsDateTime(Instant.fromEpochMilliseconds(schedule.createdAt).toLocalDateTime(timeZone))
        val lastModified = formatIcsDateTime(Instant.fromEpochMilliseconds(schedule.updatedAt).toLocalDateTime(timeZone))
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
        sb.appendLine(if (schedule.isChecked) "STATUS:COMPLETED" else "STATUS:CONFIRMED")
        if (schedule.reminderEnabled && schedule.reminderTime != null) {
            sb.append(buildVAlarm(schedule))
        }
        sb.appendLine(VEVENT_END)
        return sb.toString()
    }

    private fun buildVAlarm(schedule: ScheduleEntity): String {
        val reminderTime = schedule.reminderTime ?: return ""
        val reminderDateTime = Instant.fromEpochMilliseconds(reminderTime).toLocalDateTime(timeZone)
        val triggerTime = formatIcsDateTime(reminderDateTime)
        return buildString {
            appendLine(VALARM_BEGIN)
            appendLine("ACTION:DISPLAY")
            appendLine("DESCRIPTION:${escapeIcsText(schedule.title)}")
            appendLine("TRIGGER;VALUE=DATE-TIME:${triggerTime}Z")
            appendLine(VALARM_END)
        }
    }

    private fun formatIcsDate(date: LocalDate): String =
        "${date.year}${date.monthNumber.toString().padStart(2, '0')}${date.dayOfMonth.toString().padStart(2, '0')}"

    private fun formatIcsDateTime(dt: LocalDateTime): String =
        "${formatIcsDate(dt.date)}T${dt.hour.toString().padStart(2, '0')}${dt.minute.toString().padStart(2, '0')}${dt.second.toString().padStart(2, '0')}"

    private fun escapeIcsText(text: String): String =
        text.replace("\\", "\\\\").replace(",", "\\,").replace(";", "\\;").replace("\n", "\\n").replace("\r", "")

    private fun parseIcsContent(content: String): Result<List<ScheduleEntity>> {
        if (!content.contains(VCALENDAR_BEGIN) || !content.contains(VCALENDAR_END)) {
            return Result.failure(IllegalArgumentException("不是有效的 iCalendar 文件"))
        }
        val schedules = mutableListOf<ScheduleEntity>()
        val eventPattern = Regex("BEGIN:VEVENT(.*?)END:VEVENT", RegexOption.DOT_MATCHES_ALL)
        for (eventMatch in eventPattern.findAll(content)) {
            parseVEvent(eventMatch.groupValues[1])?.let { schedules.add(it) }
        }
        return Result.success(schedules)
    }

    private fun parseVEvent(content: String): ScheduleEntity? {
        val lines = content.lines().map { it.trim() }.filter { it.isNotBlank() }
        var title = ""
        var description = ""
        var date: Long = 0
        var isChecked = false
        var createdAt = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        var reminderEnabled = false
        var reminderTime: Long? = null

        for (line in lines) {
            when {
                line.startsWith("SUMMARY:") -> title = unescapeIcsText(line.substringAfter("SUMMARY:"))
                line.startsWith("DESCRIPTION:") -> description = unescapeIcsText(line.substringAfter("DESCRIPTION:"))
                line.startsWith("DTSTART") -> date = parseDtStart(line)
                line.startsWith("STATUS:COMPLETED") -> isChecked = true
                line.startsWith("CREATED:") -> parseDateTime(line.substringAfter("CREATED:"))?.let { createdAt = it }
                line.startsWith("TRIGGER") -> {
                    reminderEnabled = true
                    reminderTime = parseTrigger(line, date)
                }
            }
        }
        if (title.isBlank() || date == 0L) return null

        return ScheduleEntity(
            id = 0,
            title = title,
            date = date,
            description = description,
            isChecked = isChecked,
            createdAt = createdAt,
            updatedAt = kotlinx.datetime.Clock.System.now().toEpochMilliseconds(),
            reminderEnabled = reminderEnabled,
            reminderTime = reminderTime
        )
    }

    private fun parseDtStart(line: String): Long {
        return try {
            val value = line.substringAfter(":")
            when {
                value.length == 8 -> parseIcsDate(value).atStartOfDayIn(timeZone).toEpochMilliseconds()
                else -> {
                    val cleanValue = value.replace("Z", "").take(15)
                    parseIcsDateTime(cleanValue).toInstant(timeZone).toEpochMilliseconds()
                }
            }
        } catch (e: Exception) {
            0L
        }
    }

    private fun parseIcsDate(s: String): LocalDate {
        require(s.length >= 8)
        return LocalDate(s.substring(0, 4).toInt(), s.substring(4, 6).toInt(), s.substring(6, 8).toInt())
    }

    private fun parseIcsDateTime(s: String): LocalDateTime {
        require(s.length >= 15)
        val date = parseIcsDate(s.substring(0, 8))
        val hour = s.substring(9, 11).toInt()
        val minute = s.substring(11, 13).toInt()
        val second = s.substring(13, 15).toIntOrNull() ?: 0
        return LocalDateTime(date, LocalTime(hour, minute, second))
    }

    private fun parseDateTime(value: String): Long? {
        return try {
            val cleanValue = value.replace("Z", "").take(15)
            parseIcsDateTime(cleanValue).toInstant(timeZone).toEpochMilliseconds()
        } catch (e: Exception) {
            null
        }
    }

    private fun parseTrigger(line: String, eventDate: Long): Long? {
        return try {
            when {
                line.contains("VALUE=DATE-TIME") -> {
                    val value = line.substringAfter(":").replace("Z", "").take(15)
                    parseIcsDateTime(value).toInstant(timeZone).toEpochMilliseconds()
                }
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
            null
        }
    }

    private fun unescapeIcsText(text: String): String =
        text.replace("\\n", "\n").replace("\\,", ",").replace("\\;", ";").replace("\\\\", "\\")
}
