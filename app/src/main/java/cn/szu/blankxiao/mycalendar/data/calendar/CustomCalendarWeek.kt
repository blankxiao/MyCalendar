package cn.szu.blankxiao.mycalendar.data.calendar

import java.time.LocalDate

/**
 * @author BlankXiao
 * @description CustomCalendarWeek 日历中的一周数据
 * @date 2025-12-04
 */

data class CustomCalendarWeek(
    /** 这周包含的 7 天 */
    val days: List<CustomCalendarDay>,
) {
    /** 这周的第一天 */
    val startDate: LocalDate get() = days.first().date
    
    /** 这周的最后一天 */
    val endDate: LocalDate get() = days.last().date
    
    /** 查找指定日期在这周中的索引（0-6），不存在返回 -1 */
    fun findDayIndex(date: LocalDate): Int {
        return days.indexOfFirst { it.date == date }
    }

    /** 检查这周是否包含指定日期 */
    fun contains(date: LocalDate): Boolean {
        return days.any { it.date == date }
    }
}

