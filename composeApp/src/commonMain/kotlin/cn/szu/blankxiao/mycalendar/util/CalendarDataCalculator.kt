package cn.szu.blankxiao.mycalendar.util

import cn.szu.blankxiao.mycalendar.model.calendar.CustomCalendarData
import cn.szu.blankxiao.mycalendar.model.calendar.CustomCalendarDay
import cn.szu.blankxiao.mycalendar.model.calendar.CustomCalendarWeek
import cn.szu.blankxiao.mycalendar.model.calendar.DayPosition
import cn.szu.blankxiao.mycalendar.model.calendar.OutDateStyle
import cn.szu.blankxiao.mycalendar.model.calendar.YearMonth
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus

/**
 * 日历数据计算工具
 */
object CalendarDataCalculator {

    fun calculateMonth(
        yearMonth: YearMonth,
        firstDayOfWeek: DayOfWeek,
        outDateStyle: OutDateStyle = OutDateStyle.EndOfGrid
    ): CustomCalendarData {
        val firstDayOfMonth = yearMonth.atDay(1)
        val lastDayOfMonth = yearMonth.atEndOfMonth()

        val inDays = calculateInDays(firstDayOfMonth, firstDayOfWeek)
        val monthDays = yearMonth.lengthOfMonth
        val outDays = calculateOutDays(inDays, monthDays, outDateStyle)
        val totalDays = inDays + monthDays + outDays

        val startDate = firstDayOfMonth.plus(-inDays, DateTimeUnit.DAY)
        val allDays = (0 until totalDays).map { offset ->
            val date = startDate.plus(offset, DateTimeUnit.DAY)
            val position = when {
                date < firstDayOfMonth -> DayPosition.InDate
                date > lastDayOfMonth -> DayPosition.OutDate
                else -> DayPosition.MonthDate
            }
            CustomCalendarDay(date, position)
        }

        val weeks = allDays.chunked(7).map { CustomCalendarWeek(it) }
        return CustomCalendarData(yearMonth, weeks)
    }

    private fun calculateInDays(firstDayOfMonth: LocalDate, firstDayOfWeek: DayOfWeek): Int {
        val firstDayValue = firstDayOfMonth.dayOfWeek.isoDayNumber
        val startDayValue = firstDayOfWeek.isoDayNumber
        return if (firstDayValue >= startDayValue) {
            firstDayValue - startDayValue
        } else {
            7 - (startDayValue - firstDayValue)
        }
    }

    private fun calculateOutDays(inDays: Int, monthDays: Int, outDateStyle: OutDateStyle): Int {
        val inAndMonthDays = inDays + monthDays
        val endOfRowDays = if (inAndMonthDays % 7 != 0) 7 - (inAndMonthDays % 7) else 0

        val endOfGridDays = if (outDateStyle == OutDateStyle.EndOfGrid) {
            val weeksInMonth = (inAndMonthDays + endOfRowDays) / 7
            if (weeksInMonth < 6) (6 - weeksInMonth) * 7 else 0
        } else {
            0
        }

        return endOfRowDays + endOfGridDays
    }

    fun getMonthIndex(startMonth: YearMonth, targetMonth: YearMonth): Int =
        (targetMonth.year - startMonth.year) * 12 + (targetMonth.month - startMonth.month)

    fun getMonthCount(startMonth: YearMonth, endMonth: YearMonth): Int =
        getMonthIndex(startMonth, endMonth) + 1

    fun getMonthByOffset(startMonth: YearMonth, offset: Int): YearMonth =
        startMonth.plusMonths(offset.toLong())

    fun getWeekStart(date: LocalDate, firstDayOfWeek: DayOfWeek): LocalDate {
        val dayOfWeek = date.dayOfWeek.isoDayNumber
        val firstDayValue = firstDayOfWeek.isoDayNumber
        val daysToSubtract = if (dayOfWeek >= firstDayValue) {
            dayOfWeek - firstDayValue
        } else {
            7 - (firstDayValue - dayOfWeek)
        }
        return date.plus(-daysToSubtract, DateTimeUnit.DAY)
    }

    fun getWeekIndex(startDate: LocalDate, targetDate: LocalDate, firstDayOfWeek: DayOfWeek): Int {
        val startWeekStart = getWeekStart(startDate, firstDayOfWeek)
        val targetWeekStart = getWeekStart(targetDate, firstDayOfWeek)
        val daysBetween = targetWeekStart.toEpochDays().toInt() - startWeekStart.toEpochDays().toInt()
        return (daysBetween / 7)
    }

    fun getTotalWeekCount(startDate: LocalDate, endDate: LocalDate, firstDayOfWeek: DayOfWeek): Int =
        getWeekIndex(startDate, endDate, firstDayOfWeek) + 1

    fun getDateByWeekIndex(startDate: LocalDate, weekIndex: Int, firstDayOfWeek: DayOfWeek): LocalDate {
        val startWeekStart = getWeekStart(startDate, firstDayOfWeek)
        return startWeekStart.plus(weekIndex * 7, DateTimeUnit.DAY)
    }
}
