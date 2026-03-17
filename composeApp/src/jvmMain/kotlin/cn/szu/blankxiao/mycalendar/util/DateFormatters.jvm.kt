package cn.szu.blankxiao.mycalendar.util

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal actual fun formatLocalDateImpl(date: LocalDate, pattern: String): String {
    val instant = date.atStartOfDayIn(TimeZone.currentSystemDefault())
    return SimpleDateFormat(pattern, Locale.CHINA).format(Date(instant.toEpochMilliseconds()))
}

internal actual fun formatLocalDateTimeImpl(dt: LocalDateTime, pattern: String): String {
    val instant = dt.toInstant(TimeZone.currentSystemDefault())
    return SimpleDateFormat(pattern, Locale.CHINA).format(Date(instant.toEpochMilliseconds()))
}

internal actual fun parseLocalDateImpl(dateString: String, pattern: String): LocalDate {
    val date = SimpleDateFormat(pattern, Locale.ROOT).parse(dateString)!!
    val instant = Instant.fromEpochMilliseconds(date.time)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
}

internal actual fun parseLocalDateTimeImpl(dateString: String, pattern: String): LocalDateTime {
    val date = SimpleDateFormat(pattern, Locale.ROOT).parse(dateString)!!
    val instant = Instant.fromEpochMilliseconds(date.time)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault())
}
