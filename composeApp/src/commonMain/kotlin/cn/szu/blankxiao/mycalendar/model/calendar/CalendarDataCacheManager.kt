package cn.szu.blankxiao.mycalendar.model.calendar

import cn.szu.blankxiao.mycalendar.util.CalendarDataCalculator
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

/**
 * 日历数据缓存管理器（纯逻辑，可迁 shared）
 */
class CalendarDataCacheManager(
    private val startDate: LocalDate,
    private val startMonth: YearMonth,
    private val firstDayOfWeek: DayOfWeek,
    private val outDateStyle: OutDateStyle
) {
    private val monthCache = mutableMapOf<Int, CustomCalendarData>()
    private val weekCache = mutableMapOf<Int, CustomCalendarData>()

    fun getMonthData(monthIndex: Int): CustomCalendarData {
        return monthCache.getOrPut(monthIndex) {
            val month = CalendarDataCalculator.getMonthByOffset(startMonth, monthIndex)
            CalendarDataCalculator.calculateMonth(month, firstDayOfWeek, outDateStyle)
        }
    }

    fun getWeekData(weekIndex: Int): CustomCalendarData {
        return weekCache.getOrPut(weekIndex) {
            val weekStartDate = CalendarDataCalculator.getDateByWeekIndex(
                startDate, weekIndex, firstDayOfWeek
            )
            val monthIndex = CalendarDataCalculator.getMonthIndex(
                startMonth, YearMonth.from(weekStartDate)
            )
            val monthData = getMonthData(monthIndex)
            val weekIndexInMonth = monthData.calDateIndexInWeeks(weekStartDate)

            monthData.copy(
                yearMonth = monthData.yearMonth,
                weeks = listOf(monthData.weeks[weekIndexInMonth.coerceAtLeast(0)])
            )
        }
    }

    fun clear() {
        monthCache.clear()
        weekCache.clear()
    }

    val monthCacheSize: Int get() = monthCache.size
    val weekCacheSize: Int get() = weekCache.size
}
