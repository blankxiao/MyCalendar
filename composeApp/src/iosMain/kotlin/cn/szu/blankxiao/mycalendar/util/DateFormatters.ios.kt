package cn.szu.blankxiao.mycalendar.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames

private val CHINESE_DAY_OF_WEEK = DayOfWeekNames(
    "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"
)

private val DATE_FORMAT_M_DAY_EEEE = LocalDate.Format {
    monthNumber()
    chars("月")
    dayOfMonth()
    chars(" ")
    dayOfWeek(CHINESE_DAY_OF_WEEK)
}

private val DATE_FORMAT_YYYY_M_DAY_EEEE = LocalDate.Format {
    year()
    chars("年")
    monthNumber()
    chars("月")
    dayOfMonth()
    chars(" ")
    dayOfWeek(CHINESE_DAY_OF_WEEK)
}

private val DATE_FORMAT_ISO = LocalDate.Format {
    year()
    chars("-")
    monthNumber()
    chars("-")
    dayOfMonth()
}

private val DATETIME_FORMAT_ISO = LocalDateTime.Format {
    date(DATE_FORMAT_ISO)
    chars(" ")
    hour()
    chars(":")
    minute()
}

private fun getDateFormat(pattern: String) = when (pattern) {
    "M月d日 EEEE" -> DATE_FORMAT_M_DAY_EEEE
    "yyyy年M月d日 EEEE" -> DATE_FORMAT_YYYY_M_DAY_EEEE
    "yyyy-MM-dd" -> DATE_FORMAT_ISO
    else -> {
        // 回退到 ISO 格式
        DATE_FORMAT_ISO
    }
}

private fun getDateTimeFormat(pattern: String) = when (pattern) {
    "yyyy-MM-dd HH:mm" -> DATETIME_FORMAT_ISO
    else -> DATETIME_FORMAT_ISO
}

internal actual fun formatLocalDateImpl(date: LocalDate, pattern: String): String {
    return date.format(getDateFormat(pattern))
}

internal actual fun formatLocalDateTimeImpl(dt: LocalDateTime, pattern: String): String {
    return dt.format(getDateTimeFormat(pattern))
}

internal actual fun parseLocalDateImpl(dateString: String, pattern: String): LocalDate {
    return LocalDate.parse(dateString, getDateFormat(pattern))
}

internal actual fun parseLocalDateTimeImpl(dateString: String, pattern: String): LocalDateTime {
    return LocalDateTime.parse(dateString, getDateTimeFormat(pattern))
}
