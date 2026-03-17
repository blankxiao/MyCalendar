package cn.szu.blankxiao.mycalendar.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

/**
 * 日期格式化 expect/actual
 * common 定义接口，androidMain 用 SimpleDateFormat，iosMain 用 kotlinx-datetime
 */

internal expect fun formatLocalDateImpl(date: LocalDate, pattern: String): String
internal expect fun formatLocalDateTimeImpl(dt: LocalDateTime, pattern: String): String
internal expect fun parseLocalDateImpl(dateString: String, pattern: String): LocalDate
internal expect fun parseLocalDateTimeImpl(dateString: String, pattern: String): LocalDateTime

/**
 * LocalDate 格式化扩展
 */
fun LocalDate.formatForDisplay(pattern: String = "M月d日 EEEE"): String =
    formatLocalDateImpl(this, pattern)

/**
 * LocalDateTime 格式化扩展
 */
fun LocalDateTime.formatForDisplay(pattern: String = "yyyy-MM-dd HH:mm"): String =
    formatLocalDateTimeImpl(this, pattern)

/**
 * 解析日期字符串为 LocalDate
 */
fun parseLocalDate(dateString: String, pattern: String): LocalDate =
    parseLocalDateImpl(dateString, pattern)

/**
 * 解析日期时间字符串为 LocalDateTime
 */
fun parseLocalDateTime(dateString: String, pattern: String): LocalDateTime =
    parseLocalDateTimeImpl(dateString, pattern)
