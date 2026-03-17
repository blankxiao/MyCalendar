package cn.szu.blankxiao.mycalendar.model.calendar

import kotlinx.datetime.LocalDate

/**
 * 日历中的单个日期
 */
data class CustomCalendarDay(
    val date: LocalDate,
    val position: DayPosition
)
