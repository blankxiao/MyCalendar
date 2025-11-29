package cn.szu.blankxiao.mycalendar.data.calendar

import java.time.LocalDate
import java.time.YearMonth

/**
 * @author BlankXiao
 * @description CustomMonth 日历中的一个月数据
 * @date 2025-11-29 18:45
 */

data class CustomCalendarMonth(
	val yearMonth: YearMonth,
	val weeks: List<List<CustomCalendarDay>>
) {
	/** 当前月份的周数 */
	val weekCount: Int get() = weeks.size

	/** 获取指定周 */
	fun getWeek(index: Int): List<CustomCalendarDay>? = weeks.getOrNull(index)

	/** 查找包含指定日期的周索引 */
	fun findWeekIndex(date: LocalDate): Int {
		return weeks.indexOfFirst { week ->
			week.any { it.date == date && it.position == DayPosition.MonthDate }
		}
	}
}