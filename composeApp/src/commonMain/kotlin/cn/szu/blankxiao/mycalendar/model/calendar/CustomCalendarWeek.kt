package cn.szu.blankxiao.mycalendar.model.calendar

import kotlinx.datetime.LocalDate

/**
 * 日历中的一周数据
 */
data class CustomCalendarWeek(
    val days: List<CustomCalendarDay>
) {
    val startDate: LocalDate get() = days.first().date
    val endDate: LocalDate get() = days.last().date

    fun findDayIndex(date: LocalDate): Int = days.indexOfFirst { it.date == date }

    fun contains(date: LocalDate): Boolean = days.any { it.date == date }
}
