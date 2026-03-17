package cn.szu.blankxiao.mycalendar.model.calendar

import kotlinx.datetime.LocalDate

/**
 * LocalDate 扩展：获取 YearMonth
 */
val LocalDate.yearMonth: YearMonth
    get() = YearMonth.from(this)
