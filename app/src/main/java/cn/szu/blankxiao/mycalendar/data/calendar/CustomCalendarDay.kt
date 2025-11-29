package cn.szu.blankxiao.mycalendar.data.calendar

import java.time.LocalDate

/**
 * @author BlankXiao
 * @description CustomDay 日历中的单个日期
 * @date 2025-11-29 18:45
 */
data class CustomCalendarDay(
	val date: LocalDate,
	val position: DayPosition
)