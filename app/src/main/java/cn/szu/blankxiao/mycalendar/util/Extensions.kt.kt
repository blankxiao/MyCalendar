package cn.szu.blankxiao.mycalendar.util

import java.time.LocalDate
import java.time.YearMonth

/**
 * @author BlankXiao
 * @description extentions
 * @date 2025-12-07 20:48
 */

public val LocalDate.yearMonth: YearMonth
	get() = YearMonth.of(year, month)
