package cn.szu.blankxiao.mycalendar.model.calendar

import kotlinx.datetime.LocalDate

/**
 * 日历中的一个月数据
 */
data class CustomCalendarData(
    val yearMonth: YearMonth,
    val weeks: List<CustomCalendarWeek>
) {
    val weekCount: Int get() = weeks.size

    fun getWeek(index: Int): CustomCalendarWeek = weeks[index]

    fun calDateIndexInWeeks(targetDate: LocalDate): Int =
        weeks.indexOfFirst { it.contains(targetDate) }

    fun findWeekIndex(date: LocalDate): Int =
        weeks.indexOfFirst { week ->
            week.days.any { it.date == date && it.position == DayPosition.MonthDate }
        }
}
