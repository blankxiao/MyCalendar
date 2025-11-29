package cn.szu.blankxiao.mycalendar.utils

import cn.szu.blankxiao.mycalendar.data.calendar.CustomCalendarDay
import cn.szu.blankxiao.mycalendar.data.calendar.CustomCalendarMonth
import cn.szu.blankxiao.mycalendar.data.calendar.DayPosition
import cn.szu.blankxiao.mycalendar.data.calendar.OutDateStyle
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

/**
 * @author BlankXiao
 * @description CalendarDataCalculator
 * @date 2025-11-29 18:49
 */


/**
 * 日历数据计算工具
 */
object CalendarDataCalculator {

	/**
	 * 计算指定月份的日历数据
	 *
	 * @param yearMonth 目标月份
	 * @param firstDayOfWeek 一周的第一天
	 * @param outDateStyle 月份外日期样式
	 * @return 计算好的月份数据
	 */
	fun calculateMonth(
		yearMonth: YearMonth,
		firstDayOfWeek: DayOfWeek,
		outDateStyle: OutDateStyle = OutDateStyle.EndOfGrid
	): CustomCalendarMonth {
		val firstDayOfMonth = yearMonth.atDay(1)
		val lastDayOfMonth = yearMonth.atEndOfMonth()

		// 计算需要填充的前置日期数量（InDate）
		val inDays = calculateInDays(firstDayOfMonth, firstDayOfWeek)

		// 计算需要填充的后置日期数量（OutDate）
		val monthDays = yearMonth.lengthOfMonth()
		val outDays = calculateOutDays(inDays, monthDays, outDateStyle)

		// 总天数
		val totalDays = inDays + monthDays + outDays

		// 生成所有日期
		val startDate = firstDayOfMonth.minusDays(inDays.toLong())
		val allDays = (0 until totalDays).map { offset ->
			val date = startDate.plusDays(offset.toLong())
			val position = when {
				date < firstDayOfMonth -> DayPosition.InDate
				date > lastDayOfMonth -> DayPosition.OutDate
				else -> DayPosition.MonthDate
			}
			CustomCalendarDay(date, position)
		}

		// 按周分组（每周 7 天）
		val weeks = allDays.chunked(7)

		return CustomCalendarMonth(yearMonth, weeks)
	}

	/**
	 * 在日历视图中，月份第一天之前需要填充多少天
	 */
	private fun calculateInDays(firstDayOfMonth: LocalDate, firstDayOfWeek: DayOfWeek): Int {
		// 一号是周几
		val firstDayValue = firstDayOfMonth.dayOfWeek.value
		val startDayValue = firstDayOfWeek.value
		// 如果一号不是一周的开始 则返回一号距离周一的天数
		return if (firstDayValue >= startDayValue) {
			firstDayValue - startDayValue
		} else {
			7 - (startDayValue - firstDayValue)
		}
	}

	/**
	 * 在日历视图中，月份最后一天之后需要填充多少天
	 */
	private fun calculateOutDays(inDays: Int, monthDays: Int, outDateStyle: OutDateStyle): Int {
		val inAndMonthDays = inDays + monthDays

		// 填充到行尾
		val endOfRowDays = if (inAndMonthDays % 7 != 0) {
			7 - (inAndMonthDays % 7)
		} else {
			0
		}

		// 如果是 EndOfGrid，填充到 6 周
		val endOfGridDays = if (outDateStyle == OutDateStyle.EndOfGrid) {
			val weeksInMonth = (inAndMonthDays + endOfRowDays) / 7
			if (weeksInMonth < 6) {
				(6 - weeksInMonth) * 7
			} else {
				0
			}
		} else {
			0
		}

		return endOfRowDays + endOfGridDays
	}

	/**
	 * 计算月份索引（相对于起始月份的偏移）
	 */
	fun getMonthIndex(startMonth: YearMonth, targetMonth: YearMonth): Int {
		return ChronoUnit.MONTHS.between(startMonth, targetMonth).toInt()
	}

	/**
	 * 计算月份总数
	 */
	fun getMonthCount(startMonth: YearMonth, endMonth: YearMonth): Int {
		return getMonthIndex(startMonth, endMonth) + 1
	}

	/**
	 * 根据偏移量获取月份
	 */
	fun getMonthByOffset(startMonth: YearMonth, offset: Int): YearMonth {
		return startMonth.plusMonths(offset.toLong())
	}
}
